// HTMX CSRF setup
document.addEventListener('htmx:configRequest', function(event) {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    if (csrfToken && csrfHeader) {
        event.detail.headers[csrfHeader] = csrfToken;
    }
});
