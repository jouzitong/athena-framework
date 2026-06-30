const state = {
    versions: [
        {
            id: "ver-1",
            code: "trade-regression",
            name: "Trade Regression",
            versionTag: "v2026.06.29",
            status: "Published",
            owner: "qa-platform",
            description: "交易主链路回归基线版本，覆盖登录、下单、成交回报与聚合断言。",
            createdAt: "2026-06-29 10:00:00"
        },
        {
            id: "ver-2",
            code: "funding-regression",
            name: "Funding Regression",
            versionTag: "draft-2026.06.30-a",
            status: "Draft",
            owner: "wallet-qa",
            description: "资金账户充值与划转链路草稿版。",
            createdAt: "2026-06-30 09:20:00"
        }
    ],
    flows: [
        {
            id: "flow-1",
            versionId: "ver-1",
            code: "trade-order-flow",
            name: "订单链路回归",
            status: "Active",
            description: "HTTP 下单 + WebSocket 回报 + 断言聚合",
            nodeIds: ["node-1", "node-2", "node-3", "node-4"]
        },
        {
            id: "flow-2",
            versionId: "ver-1",
            code: "trade-cancel-flow",
            name: "撤单链路回归",
            status: "Draft",
            description: "订单取消后状态变更校验",
            nodeIds: []
        }
    ],
    nodes: [
        {
            id: "node-1",
            flowId: "flow-1",
            order: 1,
            type: "HTTP",
            name: "登录获取 Token",
            target: "POST /auth/login",
            timeout: "5000ms",
            paramDefs: [
                { key: "username", label: "用户名", source: "manual", value: "qa_admin" },
                { key: "password", label: "密码", source: "manual", value: "******" }
            ],
            assertRule: "status = 200"
        },
        {
            id: "node-2",
            flowId: "flow-1",
            order: 2,
            type: "HTTP",
            name: "创建订单",
            target: "POST /trade/order/create",
            timeout: "6000ms",
            paramDefs: [
                { key: "symbol", label: "交易对", source: "manual", value: "BTC-USDT" },
                { key: "clientSeq", label: "客户端流水号", source: "auto-random", value: "" },
                { key: "amount", label: "下单数量", source: "manual", value: "0.01" }
            ],
            assertRule: "status = 200 && orderId exists"
        },
        {
            id: "node-3",
            flowId: "flow-1",
            order: 3,
            type: "WEBSOCKET",
            name: "监听成交回报",
            target: "ws://stream/order/execution",
            timeout: "10000ms",
            paramDefs: [
                { key: "subscriptionId", label: "订阅ID", source: "auto-uuid", value: "" },
                { key: "expectStatus", label: "期望状态", source: "manual", value: "FILLED" }
            ],
            assertRule: "event.status = FILLED"
        },
        {
            id: "node-4",
            flowId: "flow-1",
            order: 4,
            type: "ASSERT",
            name: "聚合结果断言",
            target: "cross-step compare",
            timeout: "3000ms",
            paramDefs: [
                { key: "executeAt", label: "执行时间", source: "auto-timestamp", value: "" }
            ],
            assertRule: "HTTP 与 WS 订单状态一致"
        }
    ],
    plans: [
        {
            id: "plan-1",
            name: "nightly-trade-regression",
            versionId: "ver-1",
            flowId: "flow-1",
            cron: "0 0 2 * * ?",
            triggerMode: "Scheduled",
            status: "Active",
            description: "每日凌晨 2 点触发交易主链路回归。"
        }
    ],
    runs: [
        {
            id: "run-1",
            planId: "plan-1",
            versionId: "ver-1",
            flowId: "flow-1",
            executionNo: "EXEC-20260629-021500",
            status: "Success",
            progress: 100,
            startedAt: "2026-06-29 02:15:00",
            finishedAt: "2026-06-29 02:15:09",
            params: {
                username: "qa_admin",
                symbol: "BTC-USDT",
                clientSeq: "736192",
                subscriptionId: "WS-9a1c-48b7",
                executeAt: "1719627300000"
            },
            stepResults: [
                { name: "登录获取 Token", type: "HTTP", status: "Success", detail: "返回 200，提取 token。" },
                { name: "创建订单", type: "HTTP", status: "Success", detail: "订单创建成功，生成 orderId=981273641。" },
                { name: "监听成交回报", type: "WEBSOCKET", status: "Success", detail: "3.4s 内收到 FILLED 事件。" },
                { name: "聚合结果断言", type: "ASSERT", status: "Success", detail: "跨节点断言通过。" }
            ],
            logLines: [
                "[02:15:00.118] 登录成功，提取 token",
                "[02:15:02.603] 创建订单成功，orderId=981273641",
                "[02:15:06.010] 收到成交回报 status=FILLED",
                "[02:15:08.426] 聚合断言通过"
            ]
        }
    ],
    schedulerProcesses: [
        {
            id: "sch-1",
            planId: "plan-1",
            name: "scheduler-trade-nightly-01",
            status: "Running",
            nextFireTime: "2026-06-30 02:00:00",
            heartbeat: "2026-06-29 17:58:20",
            log: [
                "Load plan nightly-trade-regression",
                "Bind version v2026.06.29",
                "Next fire time 2026-06-30 02:00:00"
            ]
        }
    ],
    selected: {
        tab: "overview",
        versionId: "ver-1",
        flowId: "flow-1",
        nodeId: "node-1",
        planId: "plan-1",
        runId: "run-1",
        schedulerId: "sch-1"
    },
    launchDraftParams: {},
    modal: null,
    executionTimer: null
};

const navItems = [...document.querySelectorAll(".nav-item")];
const panels = [...document.querySelectorAll(".tab-panel")];
const modal = document.getElementById("modal");
const modalTitle = document.getElementById("modal-title");
const modalEyebrow = document.getElementById("modal-eyebrow");
const modalForm = document.getElementById("modal-form");

const heroMetrics = document.getElementById("hero-metrics");
const currentContext = document.getElementById("current-context");
const versionList = document.getElementById("version-list");
const versionDetail = document.getElementById("version-detail");
const flowList = document.getElementById("flow-list");
const flowDetail = document.getElementById("flow-detail");
const nodeList = document.getElementById("node-list");
const nodeDetail = document.getElementById("node-detail");
const planList = document.getElementById("plan-list");
const planDetail = document.getElementById("plan-detail");
const launchParams = document.getElementById("launch-params");
const runList = document.getElementById("run-list");
const runDetail = document.getElementById("run-detail");
const schedulerList = document.getElementById("scheduler-list");
const schedulerDetail = document.getElementById("scheduler-detail");

const createVersionBtn = document.getElementById("create-version-btn");
const createFlowBtn = document.getElementById("create-flow-btn");
const createNodeBtn = document.getElementById("create-node-btn");
const createPlanBtn = document.getElementById("create-plan-btn");
const runPlanBtn = document.getElementById("run-plan-btn");
const closeModalBtn = document.getElementById("close-modal-btn");

function uid(prefix) {
    return `${prefix}-${Math.random().toString(36).slice(2, 8)}`;
}

function nowString() {
    return new Date().toLocaleString("zh-CN", { hour12: false });
}

function getVersion(id = state.selected.versionId) {
    return state.versions.find(item => item.id === id);
}

function getFlow(id = state.selected.flowId) {
    return state.flows.find(item => item.id === id);
}

function getNode(id = state.selected.nodeId) {
    return state.nodes.find(item => item.id === id);
}

function getPlan(id = state.selected.planId) {
    return state.plans.find(item => item.id === id);
}

function getRun(id = state.selected.runId) {
    return state.runs.find(item => item.id === id);
}

function getScheduler(id = state.selected.schedulerId) {
    return state.schedulerProcesses.find(item => item.id === id);
}

function flowsByVersion(versionId) {
    return state.flows.filter(item => item.versionId === versionId);
}

function nodesByFlow(flowId) {
    return state.nodes
        .filter(item => item.flowId === flowId)
        .sort((a, b) => a.order - b.order);
}

function plansByVersion(versionId) {
    return state.plans.filter(item => item.versionId === versionId);
}

function classForStatus(status) {
    if (["Published", "Active", "Success", "Running"].includes(status)) {
        return "success";
    }
    if (["Draft", "Pending"].includes(status)) {
        return "warn";
    }
    if (["Failed", "Stopped"].includes(status)) {
        return "danger";
    }
    return "ghost";
}

function classForType(type) {
    if (type === "HTTP") return "blue";
    if (type === "WEBSOCKET") return "violet";
    if (type === "ASSERT") return "accent";
    return "ghost";
}

function renderHeroMetrics() {
    const activeRuns = state.runs.filter(item => item.status === "Running").length;
    const totalNodes = state.nodes.length;
    const activePlans = state.plans.filter(item => item.status === "Active").length;
    const publishedVersions = state.versions.filter(item => item.status === "Published").length;
    heroMetrics.innerHTML = `
        <div class="metric-card"><strong>${state.versions.length}</strong><span>测试版本</span></div>
        <div class="metric-card"><strong>${activePlans}</strong><span>激活计划</span></div>
        <div class="metric-card"><strong>${totalNodes}</strong><span>测试节点</span></div>
        <div class="metric-card"><strong>${activeRuns}</strong><span>执行中任务</span></div>
        <div class="metric-card"><strong>${publishedVersions}</strong><span>已发布版本</span></div>
        <div class="metric-card"><strong>${state.schedulerProcesses.length}</strong><span>调度进程</span></div>
    `;
}

function renderCurrentContext() {
    const version = getVersion();
    const flow = getFlow();
    const plan = getPlan();
    currentContext.innerHTML = `
        <div class="detail-card">
            <h4>${version ? version.name : "未选择版本"}</h4>
            <div class="detail-tags">
                ${version ? `<span class="pill ${classForStatus(version.status)}">${version.status}</span>` : ""}
                ${flow ? `<span class="pill blue">${flow.name}</span>` : ""}
                ${plan ? `<span class="pill violet">${plan.name}</span>` : ""}
            </div>
            <div class="detail-grid">
                <div class="detail-row"><label>当前版本</label><span>${version ? `${version.code} / ${version.versionTag}` : "-"}</span></div>
                <div class="detail-row"><label>流程数量</label><span>${version ? flowsByVersion(version.id).length : 0}</span></div>
                <div class="detail-row"><label>节点数量</label><span>${flow ? nodesByFlow(flow.id).length : 0}</span></div>
                <div class="detail-row"><label>计划数量</label><span>${version ? plansByVersion(version.id).length : 0}</span></div>
            </div>
        </div>
    `;
}

function renderVersions() {
    versionList.innerHTML = state.versions.map(item => `
        <article class="list-item ${item.id === state.selected.versionId ? "active" : ""}" data-version-id="${item.id}">
            <strong>${item.name}</strong>
            <p>${item.code} / ${item.versionTag}</p>
            <div class="list-meta">
                <span class="pill ${classForStatus(item.status)}">${item.status}</span>
                <span class="pill ghost">${flowsByVersion(item.id).length} Flows</span>
                <span class="pill ghost">${plansByVersion(item.id).length} Plans</span>
            </div>
        </article>
    `).join("");

    const version = getVersion();
    versionDetail.innerHTML = version ? `
        <div class="detail-card">
            <h4>${version.name}</h4>
            <p class="muted-text">${version.description}</p>
            <div class="detail-tags">
                <span class="pill ${classForStatus(version.status)}">${version.status}</span>
                <span class="pill ghost">Owner: ${version.owner}</span>
            </div>
            <div class="detail-grid">
                <div class="detail-row"><label>版本号</label><span>${version.versionTag}</span></div>
                <div class="detail-row"><label>创建时间</label><span>${version.createdAt}</span></div>
                <div class="detail-row"><label>测试流程</label><span>${flowsByVersion(version.id).length}</span></div>
                <div class="detail-row"><label>执行计划</label><span>${plansByVersion(version.id).length}</span></div>
            </div>
        </div>
    ` : emptyState("请先选择一个测试版本");
}

function renderFlows() {
    const version = getVersion();
    const flows = version ? flowsByVersion(version.id) : [];
    if (!flows.find(item => item.id === state.selected.flowId) && flows[0]) {
        state.selected.flowId = flows[0].id;
    }
    flowList.innerHTML = flows.length ? flows.map(item => `
        <article class="list-item ${item.id === state.selected.flowId ? "active" : ""}" data-flow-id="${item.id}">
            <strong>${item.name}</strong>
            <p>${item.code}</p>
            <div class="list-meta">
                <span class="pill ${classForStatus(item.status)}">${item.status}</span>
                <span class="pill ghost">${nodesByFlow(item.id).length} Nodes</span>
            </div>
        </article>
    `).join("") : emptyState("当前版本下还没有测试流程。");

    const flow = getFlow();
    flowDetail.innerHTML = flow ? `
        <div class="detail-card">
            <h4>${flow.name}</h4>
            <p class="muted-text">${flow.description}</p>
            <div class="detail-tags">
                <span class="pill blue">${flow.code}</span>
                <span class="pill ${classForStatus(flow.status)}">${flow.status}</span>
            </div>
            <div class="detail-grid">
                <div class="detail-row"><label>归属版本</label><span>${version ? version.versionTag : "-"}</span></div>
                <div class="detail-row"><label>节点数量</label><span>${nodesByFlow(flow.id).length}</span></div>
                <div class="detail-row"><label>启动方式</label><span>手动/计划均支持</span></div>
            </div>
            <div class="run-steps">
                ${nodesByFlow(flow.id).map(node => `
                    <div class="run-step">
                        <header>
                            <strong>${String(node.order).padStart(2, "0")} ${node.name}</strong>
                            <span class="pill ${classForType(node.type)}">${node.type}</span>
                        </header>
                        <p class="muted-text">${node.target}</p>
                    </div>
                `).join("")}
            </div>
        </div>
    ` : emptyState("请选择一个测试流程");
}

function renderNodes() {
    const flow = getFlow();
    const nodes = flow ? nodesByFlow(flow.id) : [];
    if (!nodes.find(item => item.id === state.selected.nodeId) && nodes[0]) {
        state.selected.nodeId = nodes[0].id;
    }
    nodeList.innerHTML = nodes.length ? nodes.map(item => `
        <article class="list-item ${item.id === state.selected.nodeId ? "active" : ""}" data-node-id="${item.id}">
            <strong>${String(item.order).padStart(2, "0")} ${item.name}</strong>
            <p>${item.target}</p>
            <div class="list-meta">
                <span class="pill ${classForType(item.type)}">${item.type}</span>
                <span class="pill ghost">${item.paramDefs.length} Params</span>
            </div>
        </article>
    `).join("") : emptyState("当前流程下还没有节点。");

    const node = getNode();
    nodeDetail.innerHTML = node ? `
        <div class="detail-card">
            <h4>${node.name}</h4>
            <div class="detail-tags">
                <span class="pill ${classForType(node.type)}">${node.type}</span>
                <span class="pill ghost">Timeout ${node.timeout}</span>
            </div>
            <div class="detail-grid">
                <div class="detail-row"><label>目标</label><span class="mono">${node.target}</span></div>
                <div class="detail-row"><label>断言</label><span>${node.assertRule}</span></div>
            </div>
            <div class="node-params">
                ${node.paramDefs.map(param => `
                    <span class="pill ${param.source.startsWith("auto") ? "accent" : "ghost"}">
                        ${param.key}: ${param.source}
                    </span>
                `).join("")}
            </div>
        </div>
    ` : emptyState("请选择一个测试节点");
}

function renderPlans() {
    const version = getVersion();
    const plans = version ? plansByVersion(version.id) : [];
    if (!plans.find(item => item.id === state.selected.planId) && plans[0]) {
        state.selected.planId = plans[0].id;
    }
    planList.innerHTML = plans.length ? plans.map(item => `
        <article class="list-item ${item.id === state.selected.planId ? "active" : ""}" data-plan-id="${item.id}">
            <strong>${item.name}</strong>
            <p>${item.description}</p>
            <div class="list-meta">
                <span class="pill ${classForStatus(item.status)}">${item.status}</span>
                <span class="pill ghost">${item.cron}</span>
            </div>
        </article>
    `).join("") : emptyState("当前版本下没有执行计划。");

    const plan = getPlan();
    const flow = plan ? state.flows.find(item => item.id === plan.flowId) : null;
    planDetail.innerHTML = plan ? `
        <div class="detail-card">
            <h4>${plan.name}</h4>
            <div class="detail-tags">
                <span class="pill ${classForStatus(plan.status)}">${plan.status}</span>
                <span class="pill violet">${plan.triggerMode}</span>
            </div>
            <div class="detail-grid">
                <div class="detail-row"><label>目标版本</label><span>${version ? version.versionTag : "-"}</span></div>
                <div class="detail-row"><label>目标流程</label><span>${flow ? flow.name : "-"}</span></div>
                <div class="detail-row"><label>Cron</label><span class="mono">${plan.cron}</span></div>
            </div>
        </div>
    ` : emptyState("请选择一个执行计划");

    renderLaunchParams();
}

function renderLaunchParams() {
    const plan = getPlan();
    const flow = plan ? state.flows.find(item => item.id === plan.flowId) : null;
    const nodes = flow ? nodesByFlow(flow.id) : [];
    const params = [];
    nodes.forEach(node => {
        node.paramDefs.forEach(param => {
            if (!params.find(item => item.key === param.key)) {
                params.push({ ...param });
            }
        });
    });

    launchParams.innerHTML = params.length ? params.map(param => {
        const draft = state.launchDraftParams[param.key] || {};
        const source = draft.source || param.source;
        const value = draft.value ?? param.value ?? "";
        return `
        <div class="param-item" data-param-key="${param.key}">
            <label>${param.label} (${param.key})</label>
            <div class="param-mode">
                <button type="button" class="${source === "manual" ? "active" : ""}" data-mode="manual" data-key="${param.key}">手动</button>
                <button type="button" class="${source !== "manual" ? "active" : ""}" data-mode="auto" data-key="${param.key}">自动</button>
            </div>
            <input data-param-input="${param.key}" value="${value}" placeholder="请输入参数值或点击自动生成">
            <select data-param-source="${param.key}">
                <option value="manual" ${source === "manual" ? "selected" : ""}>manual</option>
                <option value="auto-random" ${source === "auto-random" ? "selected" : ""}>auto-random</option>
                <option value="auto-timestamp" ${source === "auto-timestamp" ? "selected" : ""}>auto-timestamp</option>
                <option value="auto-uuid" ${source === "auto-uuid" ? "selected" : ""}>auto-uuid</option>
            </select>
        </div>
    `;
    }).join("") : emptyState("当前计划没有可注入参数。");
}

function renderRuns() {
    runList.innerHTML = state.runs.map(item => `
        <article class="list-item ${item.id === state.selected.runId ? "active" : ""}" data-run-id="${item.id}">
            <strong>${item.executionNo}</strong>
            <p>${item.startedAt}</p>
            <div class="list-meta">
                <span class="pill ${classForStatus(item.status)}">${item.status}</span>
                <span class="pill ghost">${item.progress}%</span>
            </div>
        </article>
    `).join("");

    const run = getRun();
    runDetail.innerHTML = run ? `
        <div class="detail-card">
            <h4>${run.executionNo}</h4>
            <div class="detail-tags">
                <span class="pill ${classForStatus(run.status)}">${run.status}</span>
                <span class="pill ghost">Started ${run.startedAt}</span>
                ${run.finishedAt ? `<span class="pill ghost">Finished ${run.finishedAt}</span>` : ""}
            </div>
            <div class="progress-card">
                <div class="detail-row"><label>测试进度</label><span>${run.progress}%</span></div>
                <div class="progress-track"><div class="progress-bar" style="width:${run.progress}%"></div></div>
            </div>
            <div class="run-steps">
                ${run.stepResults.map(step => `
                    <div class="run-step">
                        <header>
                            <strong>${step.name}</strong>
                            <span class="pill ${classForStatus(step.status)}">${step.status}</span>
                        </header>
                        <p class="muted-text">${step.type} - ${step.detail}</p>
                    </div>
                `).join("")}
            </div>
            <div class="log-box">${run.logLines.join("\n")}</div>
        </div>
    ` : emptyState("请选择一个执行记录");
}

function renderScheduler() {
    schedulerList.innerHTML = state.schedulerProcesses.map(item => `
        <article class="list-item ${item.id === state.selected.schedulerId ? "active" : ""}" data-scheduler-id="${item.id}">
            <strong>${item.name}</strong>
            <p>${item.nextFireTime}</p>
            <div class="list-meta">
                <span class="pill ${classForStatus(item.status)}">${item.status}</span>
                <span class="pill ghost">Heartbeat ${item.heartbeat}</span>
            </div>
        </article>
    `).join("");

    const scheduler = getScheduler();
    schedulerDetail.innerHTML = scheduler ? `
        <div class="detail-card">
            <h4>${scheduler.name}</h4>
            <div class="detail-tags">
                <span class="pill ${classForStatus(scheduler.status)}">${scheduler.status}</span>
                <span class="pill ghost">Plan ${getPlan(scheduler.planId)?.name || "-"}</span>
            </div>
            <div class="detail-grid">
                <div class="detail-row"><label>下次调度</label><span>${scheduler.nextFireTime}</span></div>
                <div class="detail-row"><label>心跳时间</label><span>${scheduler.heartbeat}</span></div>
            </div>
            <div class="log-box">${scheduler.log.join("\n")}</div>
        </div>
    ` : emptyState("请选择一个调度进程");
}

function emptyState(text) {
    return `<div class="empty-state">${text}</div>`;
}

function renderAll() {
    renderHeroMetrics();
    renderCurrentContext();
    renderVersions();
    renderFlows();
    renderNodes();
    renderPlans();
    renderRuns();
    renderScheduler();
}

function switchTab(tab) {
    state.selected.tab = tab;
    navItems.forEach(item => item.classList.toggle("active", item.dataset.tab === tab));
    panels.forEach(panel => panel.classList.toggle("active", panel.dataset.panel === tab));
}

function openModal(config) {
    state.modal = config;
    modalEyebrow.textContent = config.eyebrow;
    modalTitle.textContent = config.title;
    modalForm.innerHTML = config.fields.map(field => `
        <div class="field-grid">
            <label for="${field.name}">${field.label}</label>
            ${field.type === "select"
                ? `<select id="${field.name}" name="${field.name}">
                    ${field.options.map(option => `<option value="${option.value}">${option.label}</option>`).join("")}
                   </select>`
                : field.type === "textarea"
                    ? `<textarea id="${field.name}" name="${field.name}" rows="4" placeholder="${field.placeholder || ""}">${field.value || ""}</textarea>`
                    : `<input id="${field.name}" name="${field.name}" value="${field.value || ""}" placeholder="${field.placeholder || ""}">`
            }
        </div>
    `).join("") + `
        <div class="form-actions">
            <button type="button" class="secondary-btn" id="cancel-modal-btn">取消</button>
            <button type="submit" class="primary-btn">保存</button>
        </div>
    `;
    modal.classList.remove("hidden");
    document.getElementById("cancel-modal-btn").addEventListener("click", closeModal);
}

function closeModal() {
    modal.classList.add("hidden");
    state.modal = null;
}

function handleModalSubmit(event) {
    event.preventDefault();
    if (!state.modal) return;
    const formData = new FormData(modalForm);
    const payload = Object.fromEntries(formData.entries());
    state.modal.onSubmit(payload);
    closeModal();
    renderAll();
}

function createVersion() {
    openModal({
        eyebrow: "版本计划管理",
        title: "新建测试版本",
        fields: [
            { name: "name", label: "版本名称", placeholder: "例如 Trade Regression" },
            { name: "code", label: "版本编码", placeholder: "例如 trade-regression" },
            { name: "versionTag", label: "版本号", placeholder: "例如 v2026.07.01" },
            { name: "owner", label: "负责人", placeholder: "例如 qa-platform" },
            { name: "status", label: "状态", type: "select", options: [
                { value: "Draft", label: "Draft" },
                { value: "Published", label: "Published" }
            ]},
            { name: "description", label: "说明", type: "textarea", placeholder: "描述版本目标与覆盖范围" }
        ],
        onSubmit(payload) {
            const version = {
                id: uid("ver"),
                createdAt: nowString(),
                ...payload
            };
            state.versions.unshift(version);
            state.selected.versionId = version.id;
        }
    });
}

function createFlow() {
    const version = getVersion();
    if (!version) return;
    openModal({
        eyebrow: "测试流程管理",
        title: "新建测试流程",
        fields: [
            { name: "name", label: "流程名称", placeholder: "例如 订单链路回归" },
            { name: "code", label: "流程编码", placeholder: "例如 trade-order-flow" },
            { name: "status", label: "状态", type: "select", options: [
                { value: "Draft", label: "Draft" },
                { value: "Active", label: "Active" }
            ]},
            { name: "description", label: "流程说明", type: "textarea", placeholder: "描述流程测试目标" }
        ],
        onSubmit(payload) {
            const flow = {
                id: uid("flow"),
                versionId: version.id,
                nodeIds: [],
                ...payload
            };
            state.flows.unshift(flow);
            state.selected.flowId = flow.id;
        }
    });
}

function createNode() {
    const flow = getFlow();
    if (!flow) return;
    openModal({
        eyebrow: "测试节点管理",
        title: "新增测试节点",
        fields: [
            { name: "name", label: "节点名称", placeholder: "例如 创建订单" },
            { name: "type", label: "节点类型", type: "select", options: [
                { value: "HTTP", label: "HTTP" },
                { value: "WEBSOCKET", label: "WEBSOCKET" },
                { value: "ASSERT", label: "ASSERT" }
            ]},
            { name: "target", label: "目标地址/动作", placeholder: "例如 POST /trade/order/create" },
            { name: "timeout", label: "超时时间", placeholder: "例如 5000ms" },
            { name: "paramKey", label: "主参数 key", placeholder: "例如 clientSeq" },
            { name: "paramLabel", label: "主参数标签", placeholder: "例如 客户端流水号" },
            { name: "paramSource", label: "主参数来源", type: "select", options: [
                { value: "manual", label: "manual" },
                { value: "auto-random", label: "auto-random" },
                { value: "auto-timestamp", label: "auto-timestamp" },
                { value: "auto-uuid", label: "auto-uuid" }
            ]},
            { name: "assertRule", label: "断言规则", placeholder: "例如 status = 200" }
        ],
        onSubmit(payload) {
            const node = {
                id: uid("node"),
                flowId: flow.id,
                order: nodesByFlow(flow.id).length + 1,
                name: payload.name,
                type: payload.type,
                target: payload.target,
                timeout: payload.timeout,
                assertRule: payload.assertRule,
                paramDefs: payload.paramKey ? [{
                    key: payload.paramKey,
                    label: payload.paramLabel || payload.paramKey,
                    source: payload.paramSource,
                    value: ""
                }] : []
            };
            state.nodes.push(node);
            state.selected.nodeId = node.id;
        }
    });
}

function createPlan() {
    const version = getVersion();
    const flows = flowsByVersion(version?.id);
    if (!version || !flows.length) return;
    openModal({
        eyebrow: "执行计划管理",
        title: "新建执行计划",
        fields: [
            { name: "name", label: "计划名称", placeholder: "例如 nightly-trade-regression" },
            { name: "flowId", label: "绑定流程", type: "select", options: flows.map(flow => ({ value: flow.id, label: flow.name })) },
            { name: "cron", label: "Cron 表达式", placeholder: "例如 0 0 2 * * ?" },
            { name: "triggerMode", label: "触发方式", type: "select", options: [
                { value: "Scheduled", label: "Scheduled" },
                { value: "Manual", label: "Manual" }
            ]},
            { name: "status", label: "状态", type: "select", options: [
                { value: "Draft", label: "Draft" },
                { value: "Active", label: "Active" }
            ]},
            { name: "description", label: "说明", type: "textarea", placeholder: "描述计划策略" }
        ],
        onSubmit(payload) {
            const plan = {
                id: uid("plan"),
                versionId: version.id,
                ...payload
            };
            state.plans.unshift(plan);
            state.selected.planId = plan.id;
            state.schedulerProcesses.unshift({
                id: uid("sch"),
                planId: plan.id,
                name: `scheduler-${plan.name}`,
                status: payload.status === "Active" ? "Running" : "Pending",
                nextFireTime: "待计算",
                heartbeat: nowString(),
                log: [
                    `Create scheduler process for ${plan.name}`,
                    `Bind flow ${state.flows.find(item => item.id === plan.flowId)?.name || "-"}`
                ]
            });
        }
    });
}

function generateValue(source, currentValue) {
    if (source === "manual") return currentValue || "";
    if (source === "auto-random") return String(Math.floor(100000 + Math.random() * 900000));
    if (source === "auto-timestamp") return String(Date.now());
    if (source === "auto-uuid") return `${uid("ID")}-${Math.random().toString(16).slice(2, 6)}`;
    return currentValue || "";
}

function collectLaunchParams() {
    const items = [...launchParams.querySelectorAll(".param-item")];
    const params = {};
    items.forEach(item => {
        const key = item.dataset.paramKey;
        const input = item.querySelector(`[data-param-input="${key}"]`);
        const source = item.querySelector(`[data-param-source="${key}"]`).value;
        const value = generateValue(source, input.value);
        input.value = value;
        state.launchDraftParams[key] = { source, value };
        params[key] = value;
    });
    return params;
}

function executePlan() {
    const plan = getPlan();
    if (!plan) return;
    const flow = state.flows.find(item => item.id === plan.flowId);
    if (!flow) return;
    const version = getVersion(plan.versionId);
    const nodes = nodesByFlow(flow.id);
    const params = collectLaunchParams();
    const newRun = {
        id: uid("run"),
        planId: plan.id,
        versionId: version.id,
        flowId: flow.id,
        executionNo: `EXEC-${Date.now()}`,
        status: "Running",
        progress: 0,
        startedAt: nowString(),
        finishedAt: "",
        params,
        stepResults: nodes.map(node => ({
            name: node.name,
            type: node.type,
            status: "Pending",
            detail: `${node.target} waiting`
        })),
        logLines: ["[bootstrap] init execution context", `[params] ${JSON.stringify(params)}`]
    };
    state.runs.unshift(newRun);
    state.selected.runId = newRun.id;
    switchTab("runs");
    renderAll();

    if (state.executionTimer) {
        clearInterval(state.executionTimer);
    }

    let index = 0;
    state.executionTimer = setInterval(() => {
        const run = getRun(newRun.id);
        if (!run) return;
        if (index < run.stepResults.length) {
            run.stepResults[index].status = "Running";
            run.stepResults[index].detail = `${run.stepResults[index].type} 节点执行中`;
            run.logLines.push(`[step-${index + 1}] ${run.stepResults[index].name} running`);
            renderRuns();

            setTimeout(() => {
                run.stepResults[index].status = "Success";
                run.stepResults[index].detail = `${run.stepResults[index].type} 节点执行完成`;
                run.logLines.push(`[step-${index + 1}] ${run.stepResults[index].name} success`);
                run.progress = Math.round(((index + 1) / run.stepResults.length) * 100);
                renderRuns();
            }, 350);
            index += 1;
        } else {
            run.status = "Success";
            run.progress = 100;
            run.finishedAt = nowString();
            run.logLines.push("[complete] execution finished");
            const scheduler = state.schedulerProcesses.find(item => item.planId === plan.id);
            if (scheduler) {
                scheduler.heartbeat = nowString();
                scheduler.log.push(`Trigger execution ${run.executionNo} success`);
            }
            clearInterval(state.executionTimer);
            state.executionTimer = null;
            renderAll();
        }
    }, 800);
}

function bindEvents() {
    navItems.forEach(item => {
        item.addEventListener("click", () => {
            switchTab(item.dataset.tab);
        });
    });

    createVersionBtn.addEventListener("click", createVersion);
    createFlowBtn.addEventListener("click", createFlow);
    createNodeBtn.addEventListener("click", createNode);
    createPlanBtn.addEventListener("click", createPlan);
    runPlanBtn.addEventListener("click", executePlan);
    closeModalBtn.addEventListener("click", closeModal);
    modalForm.addEventListener("submit", handleModalSubmit);

    document.body.addEventListener("click", event => {
        const versionItem = event.target.closest("[data-version-id]");
        const flowItem = event.target.closest("[data-flow-id]");
        const nodeItem = event.target.closest("[data-node-id]");
        const planItem = event.target.closest("[data-plan-id]");
        const runItem = event.target.closest("[data-run-id]");
        const schedulerItem = event.target.closest("[data-scheduler-id]");
        const modeBtn = event.target.closest("[data-mode]");

        if (versionItem) {
            state.selected.versionId = versionItem.dataset.versionId;
            const flows = flowsByVersion(state.selected.versionId);
            const plans = plansByVersion(state.selected.versionId);
            state.selected.flowId = flows[0]?.id || "";
            state.selected.nodeId = nodesByFlow(state.selected.flowId)[0]?.id || "";
            state.selected.planId = plans[0]?.id || "";
            state.launchDraftParams = {};
            renderAll();
        }

        if (flowItem) {
            state.selected.flowId = flowItem.dataset.flowId;
            state.selected.nodeId = nodesByFlow(state.selected.flowId)[0]?.id || "";
            renderAll();
        }

        if (nodeItem) {
            state.selected.nodeId = nodeItem.dataset.nodeId;
            renderAll();
        }

        if (planItem) {
            state.selected.planId = planItem.dataset.planId;
            state.launchDraftParams = {};
            renderAll();
        }

        if (runItem) {
            state.selected.runId = runItem.dataset.runId;
            renderAll();
        }

        if (schedulerItem) {
            state.selected.schedulerId = schedulerItem.dataset.schedulerId;
            renderAll();
        }

        if (modeBtn) {
            const key = modeBtn.dataset.key;
            const mode = modeBtn.dataset.mode;
            const select = document.querySelector(`[data-param-source="${key}"]`);
            const input = document.querySelector(`[data-param-input="${key}"]`);
            let source = select.value;
            if (mode === "manual") {
                source = "manual";
            } else if (source === "manual") {
                source = "auto-random";
            }
            const value = generateValue(source, input.value);
            state.launchDraftParams[key] = { source, value };
            renderLaunchParams();
        }
    });

    document.body.addEventListener("change", event => {
        const sourceSelect = event.target.closest("[data-param-source]");
        if (!sourceSelect) return;
        const key = sourceSelect.dataset.paramSource;
        const input = document.querySelector(`[data-param-input="${key}"]`);
        if (input) {
            const value = generateValue(sourceSelect.value, input.value);
            input.value = value;
            state.launchDraftParams[key] = { source: sourceSelect.value, value };
        }
    });

    document.body.addEventListener("input", event => {
        const input = event.target.closest("[data-param-input]");
        if (!input) return;
        const key = input.dataset.paramInput;
        const select = document.querySelector(`[data-param-source="${key}"]`);
        state.launchDraftParams[key] = {
            source: select ? select.value : "manual",
            value: input.value
        };
    });
}

bindEvents();
switchTab("overview");
renderAll();
