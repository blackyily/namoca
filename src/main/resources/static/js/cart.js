// Funciones del carrito persistentes en Backend (PostgreSQL)

async function addToCart(variantId, name, price, imagePath) {
    try {
        // Usamos URLSearchParams para asegurar que los parámetros se envíen como x-www-form-urlencoded
        // Esto garantiza la compatibilidad con @RequestParam en Spring Boot.
        const params = new URLSearchParams();
        params.append('variantId', variantId);
        params.append('price', price);

        const response = await fetch('/carrito/agregar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params
        });

        if (response.ok) {
            // Actualizar el icono inmediatamente tras agregar
            await updateCartIcon();
            alert('¡' + name + ' se agregó al carrito!');
        } else if (response.status === 401) {
            alert('Por favor, inicia sesión para agregar productos al carrito.');
            window.location.href = '/login?redirect=/comprar';
        } else {
            console.error('Error al agregar al carrito. Status:', response.status);
        }
    } catch (error) {
        console.error('Error de red:', error);
    }
}

// Función puente para los botones dinámicos de Thymeleaf.
function addToCartFromData(button) {
    const id = button.getAttribute('data-id');
    const nombre = button.getAttribute('data-nombre');
    const precio = parseFloat(button.getAttribute('data-precio'));
    const imagen = button.getAttribute('data-imagen');
    addToCart(id, nombre, precio, imagen);
}

// Función para actualizar el badge del carrito (círculo rojo) desde el servidor
async function updateCartIcon() {
    try {
        const response = await fetch('/carrito/count');
        if (response.ok) {
            const totalItems = await response.json();
            const cartBadges = document.querySelectorAll('.cart-badge');
            
            cartBadges.forEach(badge => {
                badge.innerText = totalItems;
                if (totalItems > 0) {
                    badge.style.display = 'inline-block';
                } else {
                    badge.style.display = 'none';
                }
            });
        }
    } catch (error) {
        console.error('Error al obtener el conteo del carrito:', error);
    }
}

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    updateCartIcon();
});
