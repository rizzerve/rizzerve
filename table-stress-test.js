import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 50 },   // ramp-up to 50 VUs
    { duration: '3m', target: 50 },   // stay at 50 VUs
    { duration: '1m', target: 0 },    // ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% of requests < 500ms
    http_req_failed: ['rate<0.01'],    // <1% errors
  },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  // pick an operation
  const rand = Math.random();
  if (rand < 0.2) {
    // CREATE
    const tableNum = `T-${__VU}-${Date.now() % 10000}`;
    let res = http.post(`${BASE}/api/tables`, null, {
      params: { tableNumber: tableNum },
    });
    check(res, {
      'create status 200': (r) => r.status === 200,
    });
  } else {
    // list & pick id
    let list = http.get(`${BASE}/api/tables`);
    check(list, { 'list status 200': (r) => r.status === 200 });
    let tables = list.json();
    if (tables.length) {
      let tbl = tables[Math.floor(Math.random() * tables.length)];
      const id = tbl.id;
      if (rand < 0.5) {
        // READ
        let r = http.get(`${BASE}/api/tables/${id}`);
        check(r, { 'get status 200': (r) => r.status === 200 });
      } else if (rand < 0.75) {
        // UPDATE
        let newNum = `U-${id}-${Date.now() % 10000}`;
        let r = http.put(`${BASE}/api/tables/${id}`, null, {
          params: { newTableNumber: newNum },
        });
        check(r, { 'update status 200': (r) => r.status === 200 });
      } else {
        // DELETE
        let r = http.del(`${BASE}/api/tables/${id}`);
        check(r, { 'delete status 200|404': (r) => r.status === 200 || r.status === 404 });
      }
    }
  }

  sleep(Math.random() * 1.5 + 0.5);
}