(() => {
  const modal = document.querySelector('[data-age-modal]');
  const openers = document.querySelectorAll('[data-open-age-gate]');
  const confirmButton = document.querySelector('[data-confirm-age]');
  const closeButtons = document.querySelectorAll('[data-close-age-gate]');
  const paymentAreas = document.querySelectorAll('[data-payment-area]');
  const storageKey = 'faceMuseAgeVerified';

  const unlockPayments = () => {
    paymentAreas.forEach((area) => {
      area.querySelector('.payment-locked')?.setAttribute('hidden', '');
      area.querySelector('.payment-content')?.removeAttribute('hidden');
    });
    loadPayPal();
  };

  const closeModal = () => {
    modal.hidden = true;
    document.body.classList.remove('modal-open');
  };

  const openModal = () => {
    modal.hidden = false;
    document.body.classList.add('modal-open');
    modal.querySelector('.modal-close')?.focus();
  };

  const loadPayPal = () => {
    if (window.paypal) {
      renderPayPal();
      return;
    }
    if (document.querySelector('script[data-paypal-sdk]')) return;
    const script = document.createElement('script');
    script.src = 'https://www.paypal.com/sdk/js?client-id=BAA_ndqaWMyQwTnDZ0LXwLQ22jdJo9uxkeXYqmBbF-hD41HMiZ83mvOcx1Kti0b6ZfXfje7Q1ievNaS8Ok&vault=true&intent=subscription';
    script.dataset.paypalSdk = 'true';
    script.onload = renderPayPal;
    document.head.appendChild(script);
  };

  const renderPayPal = () => {
    const container = document.querySelector('#paypal-button-container-P-4FD24238VX4902214NK2XP2Y');
    if (!container || container.dataset.rendered || !window.paypal) return;
    window.paypal.Buttons({
      style: { shape: 'rect', color: 'gold', layout: 'vertical', label: 'subscribe' },
      createSubscription: (data, actions) => actions.subscription.create({ plan_id: 'P-4FD24238VX4902214NK2XP2Y', quantity: 1 }),
      onApprove: (data) => alert(`Thank you for your support. Subscription ID: ${data.subscriptionID}`)
    }).render(container);
    container.dataset.rendered = 'true';
  };

  openers.forEach((button) => button.addEventListener('click', openModal));
  closeButtons.forEach((button) => button.addEventListener('click', closeModal));
  confirmButton?.addEventListener('click', () => {
    localStorage.setItem(storageKey, 'true');
    unlockPayments();
    closeModal();
  });
  modal?.addEventListener('click', (event) => {
    if (event.target === modal) closeModal();
  });
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape' && !modal.hidden) closeModal();
  });

  if (localStorage.getItem(storageKey) === 'true') unlockPayments();
})();
