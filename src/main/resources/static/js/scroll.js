function initCategoryScroll() {
    document.querySelectorAll(".category-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-target");
            const target = document.getElementById(targetId);
            if (target) {
                target.scrollIntoView({ behavior: "smooth" });
            }
        });
    });
}
