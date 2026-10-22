(() => {
  const config = window.MUSE3_CONFIG || {};
  const minimumAge = Number.isInteger(Number(config.minimumAge)) && Number(config.minimumAge) >= 18
    ? Number(config.minimumAge)
    : 18;

  const safeHttpsUrl = (value) => {
    if (typeof value !== "string" || !value.trim()) return null;
    try {
      const url = new URL(value.trim());
      if (url.protocol !== "https:" || !url.hostname || url.username || url.password) return null;
      return url;
    } catch {
      return null;
    }
  };

  // Promote the generated managed artwork only inside the Manus Preview.
  // The packaged site keeps portable local SVG illustrations for Cloudflare/GitHub hosting.
  const isManagedPreview = location.hostname.endsWith(".manus.computer") || location.hostname.endsWith(".manus.im");
  if (isManagedPreview) {
    document.querySelectorAll("img[data-managed-src]").forEach((image) => {
      const localSource = image.getAttribute("src");
      image.addEventListener("error", () => {
        if (localSource && image.getAttribute("src") !== localSource) image.setAttribute("src", localSource);
      }, { once: true });
      image.setAttribute("src", image.dataset.managedSrc);
    });
  }

  // A centered Discord widget is created only for a configured numeric server ID.
  const serverId = String(config.discordServerId || "").trim();
  const discordPlaceholder = document.querySelector("[data-discord-placeholder]");
  const discordFrameWrap = document.querySelector("[data-discord-frame-wrap]");
  if (/^\d{6,24}$/.test(serverId) && discordFrameWrap) {
    const frame = document.createElement("iframe");
    frame.title = "Muse Number 3 Discord server widget";
    frame.loading = "lazy";
    frame.referrerPolicy = "strict-origin-when-cross-origin";
    frame.src = `https://discord.com/widget?id=${encodeURIComponent(serverId)}&theme=dark`;
    discordFrameWrap.append(frame);
    discordFrameWrap.hidden = false;
    if (discordPlaceholder) discordPlaceholder.hidden = true;
  }

  const invite = safeHttpsUrl(config.discordInviteUrl);
  const inviteAnchor = document.querySelector("[data-discord-invite]");
  if (invite && inviteAnchor && (invite.hostname === "discord.gg" || invite.hostname.endsWith(".discord.gg") || invite.hostname === "discord.com" || invite.hostname.endsWith(".discord.com"))) {
    inviteAnchor.href = invite.href;
    inviteAnchor.hidden = false;
  }

  // The payment destination stays out of the DOM until a visitor confirms age.
  const paymentUrl = safeHttpsUrl(config.paymentUrl);
  const unavailable = document.querySelector("[data-payment-unavailable]");
  const gateOpen = document.querySelector("[data-age-gate-open]");
  const paymentLink = document.querySelector("[data-payment-link]");
  const paymentStatus = document.querySelector("[data-payment-status]");
  const dialog = document.querySelector("[data-age-dialog]");
  const confirmAge = document.querySelector("[data-age-confirm]");
  const declineAge = document.querySelector("[data-age-decline]");
  const closeAge = document.querySelector("[data-age-close]");
  const ageLabel = document.querySelector("[data-required-age]");
  if (ageLabel) ageLabel.textContent = String(minimumAge);

  if (paymentUrl && gateOpen && paymentLink && dialog) {
    if (unavailable) unavailable.hidden = true;
    gateOpen.hidden = false;
    gateOpen.addEventListener("click", () => {
      if (typeof dialog.showModal === "function") dialog.showModal();
      else if (paymentStatus) paymentStatus.textContent = `Your browser needs to support dialogs to confirm you are at least ${minimumAge}.`;
    });

    const closeDialog = () => {
      if (dialog.open) dialog.close();
      gateOpen.focus();
    };
    if (closeAge) closeAge.addEventListener("click", closeDialog);
    if (declineAge) declineAge.addEventListener("click", () => {
      closeDialog();
      if (paymentStatus) paymentStatus.textContent = "No payment option was opened.";
    });
    if (confirmAge) confirmAge.addEventListener("click", () => {
      paymentLink.href = paymentUrl.href;
      paymentLink.hidden = false;
      gateOpen.hidden = true;
      if (paymentStatus) paymentStatus.textContent = "Age confirmed. The configured payment destination is now available.";
      if (dialog.open) dialog.close();
      paymentLink.focus();
    });
    dialog.addEventListener("click", (event) => {
      if (event.target === dialog) closeDialog();
    });
    dialog.addEventListener("cancel", () => {
      if (paymentStatus) paymentStatus.textContent = "No payment option was opened.";
    });
  }

  // Content remains visible if JavaScript or IntersectionObserver is unavailable.
  if ("IntersectionObserver" in window && !window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
    document.documentElement.classList.add("motion-ready");
    const observer = new IntersectionObserver((entries, currentObserver) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          currentObserver.unobserve(entry.target);
        }
      });
    }, { threshold: 0.12 });
    document.querySelectorAll("[data-reveal]").forEach((element) => observer.observe(element));
  }
})();
