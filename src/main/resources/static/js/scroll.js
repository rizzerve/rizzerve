document.querySelectorAll('.category-btn').forEach(btn => {
    btn.addEventListener('click', e => {
        const targetId = btn.getAttribute('data-target');
        document.getElementById(targetId)
            .scrollIntoView({ behavior: 'smooth' });
    });
});
