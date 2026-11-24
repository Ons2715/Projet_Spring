const formationCards = [...document.querySelectorAll(".plan-card")];
const selectedFormationLabel = document.getElementById("selected-formation-label");
const walletModalOverlay = document.getElementById("wallet-modal-overlay");
const walletModalFrame = document.getElementById("wallet-modal-frame");
const continueButton = document.querySelector(".confirm-button");
let formationRecords = [];

function getSelectedTransmission(card) {
  if (card?.dataset.typePratique !== "RECYCLAGE") return card?.dataset.transmission || "";
  return (document.querySelector('input[name="recyclage-boite"]:checked')?.value || "automatique").toUpperCase();
}

function getFormationRecord(card) {
  const transmission = getSelectedTransmission(card);
  return formationRecords.find((formation) =>
    formation.typePratique === card.dataset.typePratique && formation.transmission === transmission
  );
}

function getFormationLabel(card) {
  const gearbox = getSelectedTransmission(card) === "MANUELLE" ? "boîte manuelle" : "boîte automatique";
  return `${card.dataset.formation} - ${gearbox}`;
}

function updateSelection(card) {
  formationCards.forEach((item) => item.classList.toggle("selected", item === card));
  selectedFormationLabel.textContent = getFormationLabel(card);
}

formationCards.forEach((card) => card.addEventListener("click", () => updateSelection(card)));
document.querySelectorAll('input[name="recyclage-boite"]').forEach((input) => {
  input.addEventListener("click", (event) => event.stopPropagation());
  input.addEventListener("change", () => {
    const selected = document.querySelector(".plan-card.selected");
    if (selected) updateSelection(selected);
  });
});

continueButton.disabled = true;
const formationsReady = Api.get("/formations").then((formations) => {
  if (!Array.isArray(formations)) throw new Error("Réponse invalide du serveur des formations.");
  formationRecords = formations;
  const requiredCombinations = [
    ["FORMATION_INITIALE", "MANUELLE"],
    ["FORMATION_INITIALE", "AUTOMATIQUE"],
    ["RECYCLAGE", "MANUELLE"],
    ["RECYCLAGE", "AUTOMATIQUE"]
  ];
  const missing = requiredCombinations.find(([type, transmission]) =>
    !formationRecords.some((formation) => formation.typePratique === type && formation.transmission === transmission)
  );
  if (missing) throw new Error(`Formation manquante : ${missing[0]} ${missing[1]}.`);
  continueButton.disabled = false;
  return true;
}).catch((error) => {
  continueButton.disabled = true;
  Toast.error(error.message || "Impossible de charger les formations.");
  return false;
});

async function handlePayment() {
  if (!await formationsReady) return;
  const activeCard = document.querySelector(".plan-card.selected");
  const selectedFormation = getFormationRecord(activeCard);
  if (!selectedFormation) {
    Toast.error("Veuillez choisir une formation valide.");
    return;
  }

  const label = getFormationLabel(activeCard);
  sessionStorage.setItem("selectedFormation", label);
  sessionStorage.setItem("selectedFormationId", String(selectedFormation.id));
  sessionStorage.setItem("selectedRecyclageBoite", getSelectedTransmission(activeCard).toLowerCase());
  walletModalFrame.src = `wallet.html?embedded=1&formation=${encodeURIComponent(label)}&amount=90`;
  walletModalOverlay.classList.add("open");
  walletModalOverlay.setAttribute("aria-hidden", "false");
  document.body.classList.add("wallet-modal-open");
}

function closeWalletModal() {
  walletModalOverlay.classList.remove("open");
  walletModalOverlay.setAttribute("aria-hidden", "true");
  document.body.classList.remove("wallet-modal-open");
  setTimeout(() => { walletModalFrame.src = ""; }, 180);
}

walletModalOverlay.addEventListener("click", (event) => {
  if (event.target === walletModalOverlay) closeWalletModal();
});
document.addEventListener("keydown", (event) => {
  if (event.key === "Escape" && walletModalOverlay.classList.contains("open")) closeWalletModal();
});
window.addEventListener("message", (event) => {
  if (event.data?.type === "EduCar-wallet-close") closeWalletModal();
});

updateSelection(document.querySelector(".plan-card.selected") || formationCards[0]);
