// HTMX CSRF setup
document.addEventListener('htmx:configRequest', function(event) {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    if (csrfToken && csrfHeader) {
        event.detail.headers[csrfHeader] = csrfToken;
    }
});

// Custom cursor
(function() {
    const cursor = document.getElementById('cursor');
    const ring = document.getElementById('cursorRing');
    if (!cursor || !ring) return;

    let mx = 0, my = 0, rx = 0, ry = 0;
    let visible = false;

    document.addEventListener('mousemove', function(e) {
        mx = e.clientX;
        my = e.clientY;
        if (!visible) {
            visible = true;
            cursor.style.opacity = '1';
            ring.style.opacity = '0.6';
        }
        cursor.style.transform = 'translate(' + (mx - 6) + 'px,' + (my - 6) + 'px)';
    });

    document.addEventListener('mouseleave', function() {
        cursor.style.opacity = '0';
        ring.style.opacity = '0';
        visible = false;
    });

    function animateRing() {
        rx += (mx - rx) * 0.1;
        ry += (my - ry) * 0.1;
        ring.style.transform = 'translate(' + (rx - 18) + 'px,' + (ry - 18) + 'px)';
        requestAnimationFrame(animateRing);
    }
    animateRing();

    document.querySelectorAll('a, button').forEach(function(el) {
        el.addEventListener('mouseenter', function() {
            ring.style.width = '48px';
            ring.style.height = '48px';
            ring.style.borderColor = 'var(--accent)';
        });
        el.addEventListener('mouseleave', function() {
            ring.style.width = '36px';
            ring.style.height = '36px';
            ring.style.borderColor = 'var(--accent)';
        });
    });
})();

// Nav scroll effect
(function() {
    const nav = document.getElementById('nav');
    if (!nav) return;
    window.addEventListener('scroll', function() {
        nav.classList.toggle('scrolled', window.scrollY > 40);
    });
})();

// Reveal on scroll
(function() {
    var observer = new IntersectionObserver(function(entries) {
        entries.forEach(function(e) {
            if (e.isIntersecting) e.target.classList.add('visible');
        });
    }, { threshold: 0.1 });
    document.querySelectorAll('.reveal').forEach(function(el) {
        observer.observe(el);
    });
})();
