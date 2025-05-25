import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 50 },   // ramp-up to 50 VUs
    { duration: '2m', target: 50 },   // stay at 50 VUs
    { duration: '1m', target: 0 },    // ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% of requests < 500ms
    http_req_failed: ['rate<0.01'],    // <1% errors
  },
};
const BASE = __ENV.BASE_URL || 'http://localhost:8080';

export function setup() {
  // 1) authenticate and grab the token
  const loginRes = http.post(
    `${BASE}/admin/login`,
    JSON.stringify({ username: 'admin', password: 'admin' }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(loginRes, { 'logged in': (r) => r.status === 200 });
  return { token: loginRes.json('token') };
}

export default function (data) {
  const authHeaders = {
    Authorization: `Bearer ${data.token}`,
    'Content-Type': 'application/json',
  };

  // then use authHeaders in every call:
  // CREATE
  if (Math.random() < 0.2) {
    // CREATE: send form-urlencoded body
    const tableNum = `T-${__VU}-${Date.now() % 10000}`;
    const payload = { tableNumber: tableNum };
    let res = http.post(
      `${BASE}/api/tables`,
      payload,
      {
        headers: {
          Authorization: authHeaders.Authorization,
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    );
    check(res, { 'create status 200': (r) => r.status === 200 });
    sleep(1);
    return;
  }

  // LIST
  let list = http.get(`${BASE}/api/tables`, { headers: authHeaders });
  check(list, { 'list 200': (r) => r.status === 200 });
  const tables = list.json();
  if (!tables.length) { sleep(1); return; }

  const tbl = tables[Math.floor(Math.random() * tables.length)];
  const id = tbl.id;
  const rand = Math.random();

  if (rand < 0.5) {
    // READ
    let r = http.get(`${BASE}/api/tables/${id}`, { headers: authHeaders });
    check(r, { 'get 200': (r) => r.status === 200 });
  } else if (rand < 0.75) {
    // UPDATE: send form-urlencoded body
    const newNum = `U-${id}-${Date.now() % 10000}`;
    const payload = { newTableNumber: newNum };
    let r = http.put(
      `${BASE}/api/tables/${id}`,
      payload,
      {
        headers: {
          Authorization: authHeaders.Authorization,
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    );
    check(r, { 'update status 200': (r) => r.status === 200 });
  } else {
    // DELETE
    let r = http.del(`${BASE}/api/tables/${id}`, null, { headers: authHeaders });
    check(r, { 'delete 200|404': (r) => r.status === 200 || r.status === 404 });
  }

  sleep(Math.random() * 1.5 + 0.5);
}