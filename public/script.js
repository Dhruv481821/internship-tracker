const API = "http://localhost:8080/api/applications";

async function loadApplications() {
  const res = await fetch(API);
  const data = await res.json();
  renderTable(data);
  renderStats(data);
}

function renderStats(data) {
  const counts = {};
  data.forEach(a => counts[a.status] = (counts[a.status] || 0) + 1);
  document.getElementById("stats").innerHTML =
    `<span>Total: ${data.length}</span>` +
    Object.entries(counts).map(([k, v]) => `<span>${k}: ${v}</span>`).join("");
}

function renderTable(data) {
  const body = document.getElementById("tableBody");
  body.innerHTML = data.map(a => `
    <tr>
      <td>${a.companyName}</td>
      <td>${a.role}</td>
      <td>
        <select class="status-select" onchange="updateStatus(${a.id}, this.value)">
          ${["Applied","OA/Test","Interview","Offer","Rejected","Ghosted"].map(s =>
            `<option ${s === a.status ? "selected" : ""}>${s}</option>`).join("")}
        </select>
      </td>
      <td>${a.appliedDate || ""}</td>
      <td>${a.followUpDate || "-"}</td>
      <td>${a.notes || ""}</td>
      <td><button class="del-btn" onclick="deleteApp(${a.id})">Delete</button></td>
    </tr>
  `).join("");
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