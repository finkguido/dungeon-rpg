function comprar(paqueteId, precio) {
    fetch(`/spring/crear-preferencia`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `paqueteOro=${encodeURIComponent(paqueteId)}&monto=${encodeURIComponent(precio)}`
    })
        .then(response => {
            if (!response.ok) throw new Error('No se pudo crear la preferencia de pago');
            return response.text();
        })
        .then(initPoint => {
            window.location.href = initPoint;
        })
        .catch(error => console.error('Error:', error));
}
