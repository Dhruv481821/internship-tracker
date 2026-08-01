const API = "http://localhost:8080/api/applications";
const TERMINAL_STATUSES = ["Offer", "Rejected", "Ghosted"];
const REACHED_INTERVIEW = ["Interview", "Offer"];

async function loadApplications() {
  const res = await fetch(API);
  const data = await res.json();
  renderTable(data);
  renderStats(data);
}

function isOverdue(app) {
  if (!app.followUpDate) return false;
  if (TERMINAL_STATUSES.includes(app.status)) return false;
  const today = new Date().toISOString().split("T")[0];
  return app.followUpDate < today;
}

function renderStats(data) {
  const total = data.length;
  const counts = {};
  data.forEach(a => counts[a.status] = (counts[a.status] || 0) + 1);

  const interviewed = data.filter(a => REACHED_INTERVIEW.includes(a.status)).length;
  const offers = counts["Offer"] || 0;
  const overdueCount = data.filter(isOverdue).length;

  const interviewRate = total ? Math.round((interviewed / total) * 100) : 0;
  const offerRate = total ? Math.round((offers / total) * 100) : 0;

  document.getElementById("stats").innerHTML = `
    <span>Total: ${total}</span>
    <span>Interview rate: ${interviewRate}%</span>
    <span>Offer rate: ${offerRate}%</span>
    ${overdueCount > 0 ? `<span class="stat-warning">Needs follow-up: ${overdueCount}</span>` : ""}
  `;
}

function renderTable(data) {
  const body = document.getElementById("tableBody");
  body.innerHTML = data.map(a => {
    const overdue = isOverdue(a);
    return `
    <tr class="${overdue ? "row-overdue" : ""}">
      <td>${a.companyName}</td>
      <td>${a.role}</td>
      <td>
        <select class="status-select" onchange="updateStatus(${a.id}, this.value)">
          ${["Applied","OA/Test","Interview","Offer","Rejected","Ghosted"].map(s =>
            `<option ${s === a.status ? "selected" : ""}>${s}</option>`).join("")}
        </select>
      </td>
      <td>${a.appliedDate || ""}</td>
      <td>${a.followUpDate || "-"} ${overdue ? '<span class="badge-overdue">Follow up</span>' : ""}</td>
      <td>${a.notes || ""}</td>
      <td><button class="del-btn" onclick="deleteApp(${a.id})">Delete</button></td>
    </tr>
  `;
  }).join("");
}

document.getElementById("addForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const body = {
    companyName: document.getElementById("companyName").value,
    role: document.getElementById("role").value,
    status: document.getElementById("status").value,
    appliedDate: document.getElementById("appliedDate").value,
    followUpDate: document.getElementById("followUpDate").value || null,
    notes: document.getElementById("notes").value
  };
  await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  e.target.reset();
  loadApplications();
});

async function updateStatus(id, status) {
  await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status })
  });
  loadApplications();
}

async function deleteApp(id) {
  if (!confirm("Delete this application?")) return;
  await fetch(`${API}/${id}`, { method: "DELETE" });
  loadApplications();
}

loadApplications();