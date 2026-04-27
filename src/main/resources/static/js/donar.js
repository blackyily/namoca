document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('slider-container');
    if (!container) return; // Salir si no estamos en la página de donar

    const svg = container.querySelector('.slider-svg');
    const progressPath = document.getElementById('progressPath');
    const thumb = document.getElementById('sliderThumb');
    const hitbox = document.getElementById('hitboxPath');
    const amountDisplay = document.getElementById('amount-display');
    const amountDesc = document.getElementById('amount-desc');
    const impactImage = document.getElementById('impact-image');

    const minAmount = 60;
    const maxAmount = 1000;
    const centerX = 100;
    const centerY = 20;
    const radius = 80;

    const pathLength = progressPath.getTotalLength();
    progressPath.style.strokeDasharray = pathLength;

    let isDragging = false;

    function updateSlider(progress) {
        progress = Math.max(0, Math.min(1, progress));

        const angle = progress * Math.PI;
        const thumbX = centerX - Math.cos(angle) * radius;
        const thumbY = centerY + Math.sin(angle) * radius;

        thumb.setAttribute('cx', thumbX);
        thumb.setAttribute('cy', thumbY);

        const offset = pathLength - (progress * pathLength);
        progressPath.style.strokeDashoffset = offset;

        const currentAmount = minAmount + (maxAmount - minAmount) * progress;
        updateDOM(currentAmount);
    }

    let lastRange = 0;
    function updateDOM(amount) {
        const roundedAmount = Math.round(amount);
        amountDisplay.textContent = `$${roundedAmount.toFixed(2)}`;

        let range = 1;
        let src = '/img/moneda.PNG';
        let desc = 'Una pastilla namoca';

        if (roundedAmount <= 60) {
            range = 1;
            src = '/img/moneda.PNG';
            desc = 'Una pastilla namoca';
        } else if (roundedAmount <= 300) {
            range = 2;
            src = '/img/monedas.PNG';
            desc = 'Varias pastillas namoca';
        }
        else if (roundedAmount <= 600) {
            range = 2;
            src = '/img/masmonedas.PNG';
            desc = '10 paquetes de pastillas namoca';
        }
        else {
            range = 3;
            src = '/img/muchasmonedas.PNG';
            desc = 'Suministro mensual de pastillas';
        }

        amountDesc.textContent = desc;

        if (range !== lastRange) {
            impactImage.style.opacity = 0;
            setTimeout(() => {
                impactImage.src = src;
                impactImage.style.opacity = 1;
            }, 200);
            lastRange = range;
        }
    }

    function handleInteraction(event) {
        if (!isDragging) return;
        event.preventDefault();

        let clientX, clientY;
        if (event.touches) {
            clientX = event.touches[0].clientX;
            clientY = event.touches[0].clientY;
        } else {
            clientX = event.clientX;
            clientY = event.clientY;
        }

        const rect = svg.getBoundingClientRect();
        const x = clientX - rect.left;
        const y = clientY - rect.top;
        const scaleX = 200 / rect.width;
        const scaleY = 110 / rect.height;
        const svgX = x * scaleX;
        const svgY = y * scaleY;

        // Calcular vector desde el centro
        const dx = centerX - svgX;
        const dy = svgY - centerY;

        // Calcular ángulo
        let angle = Math.atan2(dy, dx);

        // Limitar ángulo al rango superior del semicírculo (0 a PI)
        if (angle < 0) {
            if (svgX < centerX) {
                angle = 0; // Izquierda
            } else {
                angle = Math.PI; // Derecha
            }
        }

        // Progreso es ángulo normalizado (0 a 1)
        const progress = angle / Math.PI;
        updateSlider(progress);
    }

    const startDrag = (e) => {
        isDragging = true;
        handleInteraction(e);
    };

    const stopDrag = () => {
        isDragging = false;
    };

    // Eventos en el contenedor SVG
    if (svg) {
        svg.addEventListener('mousedown', startDrag);
        window.addEventListener('mousemove', handleInteraction);
        window.addEventListener('mouseup', stopDrag);

        // Soporte Touch
        svg.addEventListener('touchstart', startDrag, { passive: false });
        window.addEventListener('touchmove', handleInteraction, { passive: false });
        window.addEventListener('touchend', stopDrag);

        // Inicializar (0 progreso)
        updateSlider(0);
    }
});
