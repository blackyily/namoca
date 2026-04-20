// Funciones del carrito usando localStorage

function initCart() {
    updateCartIcon();
    // Identifica la página de carrito y carga los elementos
    const cartContainer = document.getElementById('cart-items-container');
    if (cartContainer) {
        renderCartItems();
    }
}

function addToCart(id, name, price, imagePath) {
    let cart = JSON.parse(localStorage.getItem('namoca_cart')) || [];
    
    // Verificar si ya existe
    const existingItemIndex = cart.findIndex(item => item.id === id);
    if (existingItemIndex > -1) {
        cart[existingItemIndex].quantity += 1;
    } else {
        cart.push({
            id: id,
            name: name,
            price: price,
            image: imagePath,
            quantity: 1
        });
    }
    
    localStorage.setItem('namoca_cart', JSON.stringify(cart));
    updateCartIcon();
}

function updateCartIcon() {
    let cart = JSON.parse(localStorage.getItem('namoca_cart')) || [];
    const totalItems = cart.reduce((total, item) => total + item.quantity, 0);
    
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

function renderCartItems() {
    const listContainer = document.getElementById('cart-items-container');
    const totalContainer = document.getElementById('cart-total');
    let cart = JSON.parse(localStorage.getItem('namoca_cart')) || [];
    
    if (cart.length === 0) {
        listContainer.innerHTML = '<p class="text-center mt-5 mb-5 fs-4">El carrito está vacío.</p>';
        totalContainer.innerText = '$0.00';
        return;
    }

    listContainer.innerHTML = '';
    let total = 0;

    cart.forEach((item, index) => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        listContainer.innerHTML += `
            <div class="d-flex align-items-center justify-content-between p-3 border-bottom">
                <div class="d-flex align-items-center gap-3">
                    <img src="${item.image}" alt="${item.name}" width="50" height="70" style="object-fit: cover;">
                    <div>
                        <h5 class="m-0 fw-bold">${item.name}</h5>
                        <p class="m-0 text-muted">$${item.price.toFixed(2)} x ${item.quantity}</p>
                    </div>
                </div>
                <div class="d-flex align-items-center gap-3">
                    <span class="fs-5 fw-bold">$${itemTotal.toFixed(2)}</span>
                    <button class="btn btn-sm btn-outline-danger" onclick="removeFromCart(${index})"><i class="bi bi-trash"></i></button>
                </div>
            </div>
        `;
    });

    totalContainer.innerText = '$' + total.toFixed(2);
}

function removeFromCart(index) {
    let cart = JSON.parse(localStorage.getItem('namoca_cart')) || [];
    cart.splice(index, 1);
    localStorage.setItem('namoca_cart', JSON.stringify(cart));
    updateCartIcon();
    renderCartItems();
}

function clearCart() {
    localStorage.removeItem('namoca_cart');
    updateCartIcon();
    renderCartItems();
}

document.addEventListener('DOMContentLoaded', initCart);
