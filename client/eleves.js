function escapeHtml(value) {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function formatDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleDateString("fr-FR");
}

function normalizeStudent(student) {
  const formation = student.formation_nom || student.type_formation || student.formation || "-";

  return {
    id: student.id,
    nom: student.nom || `${student.firstName || ""} ${student.lastName || ""}`.trim() || "Candidat",
    email: student.email || "-",
    telephone: student.telephone || student.phone || "-",
    photo: student.photo || "",
    avatar: student.avatar || "",
    formation: ["1 heure", "2 heures"].includes(formation) ? "Formation non renseignee" : formation,
    heuresEffectuees: student.heures_effectuees,
    heuresTotales: student.heures_totales,
    dateInscription: student.date_inscription
  };
}

function getInitials(name = "") {
  const parts = String(name).trim().split(/\s+/).filter(Boolean);
  return `${parts[0]?.charAt(0) || ""}${parts[1]?.charAt(0) || ""}`.toUpperCase() || "EC";
}

function getStudentPhoto(student) {
  return ProfilePhotos.get(student.email) || student.photo || student.avatar || "";
}

function renderStudents(students) {
  const normalizedStudents = students.map(normalizeStudent);
  const tableBody = document.getElementById("students-table-body");
  const badge = document.getElementById("students-total-badge");

  badge.textContent = `${normalizedStudents.length} candidat${normalizedStudents.length > 1 ? "s" : ""}`;

  if (!normalizedStudents.length) {
    tableBody.innerHTML = `
      <tr>
        <td colspan="6">
          <div class="monitor-empty-state">Aucun candidat n'est affecte a votre espace pour le moment.</div>
        </td>
      </tr>
    `;
    return;
  }

  tableBody.innerHTML = normalizedStudents.map((student) => {
    const hoursDone = student.heuresEffectuees ?? 0;
    const totalHours = student.heuresTotales ?? "-";
    const photo = getStudentPhoto(student);

    return `
      <tr>
        <td>
          <div class="monitor-student-cell">
            <div class="monitor-student-photo">
              ${photo
                ? `<img src="${escapeHtml(photo)}" alt="Photo de profil" />`
                : escapeHtml(getInitials(student.nom))}
            </div>
            <div>
              <strong>${escapeHtml(student.nom)}</strong>
              <small>#${escapeHtml(student.id || "-")}</small>
            </div>
          </div>
        </td>
        <td>${escapeHtml(student.email)}</td>
        <td>
          ${student.telephone && student.telephone !== "-"
            ? `<a class="monitor-phone-link" href="tel:${escapeHtml(student.telephone)}">${escapeHtml(student.telephone)}</a>`
            : "-"}
        </td>
        <td>${escapeHtml(student.formation)}</td>
        <td>
          <span class="badge badge-accent">${escapeHtml(hoursDone)} / ${escapeHtml(totalHours)} h</span>
        </td>
        <td>${escapeHtml(formatDate(student.dateInscription))}</td>
      </tr>
    `;
  }).join("");
}

async function loadMonitorStudents() {
  const monitorUser = Auth.get() || {};

  if (!monitorUser.id) {
    Toast.error("Connectez-vous avec un compte moniteur.");
    renderStudents([]);
    return;
  }

  try {
    const students = await Api.get("/students/monitor/me");
    renderStudents(students);
  } catch (error) {
    renderStudents([]);
    Toast.error(error.message || "Impossible de charger les candidats depuis le serveur.");
  }
}

document.getElementById("students-logout-link").addEventListener("click", function () {
  Auth.clear();
});

loadMonitorStudents();
