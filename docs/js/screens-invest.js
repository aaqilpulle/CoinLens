// CoinLens PWA — Super & Shares Screen (+ ShareDetailView) and Other Assets Screen
import {
  formatCurrency,
  groupShareHoldings,
  buildPurchaseAwareTimelinePoints,
  formatParcelDate,
  formatShareUnits,
  getShareLogoUrl,
  OTHER_ASSET_TYPES,
  parseOtherAssetType,
  depreciatedAssetValue,
  projectedAssetValueAfterYears,
  hexLongToCss
} from "./finance.js";
import { renderShareTimelineChart, attachScrubHandlers } from "./charts.js";
import { esc } from "./screens-core.js";

function renderShareLogoBadge(code, market) {
  const logoUrl = getShareLogoUrl(code);
  const ticker = esc(String(code || "?").toUpperCase().slice(0, 4));
  const mBadge = market === "US" ? "border-color:rgba(192,132,252,0.55);color:#C084FC;background:rgba(192,132,252,0.16);" : "";
  if (logoUrl) {
    return `
      <div class="ticker-badge" style="overflow:hidden;position:relative;${mBadge}">
        <img src="${esc(logoUrl)}" alt="${ticker}" style="width:100%;height:100%;object-fit:cover;" onerror="this.style.display='none';this.nextElementSibling.style.display='flex'" />
        <span style="display:none;width:100%;height:100%;align-items:center;justify-content:center;">${ticker}</span>
      </div>
    `;
  }
  return `<div class="ticker-badge" style="${mBadge}">${ticker}</div>`;
}

// ============================================================================
// 4. SUPER & SHARES SCREEN (+ SHARE DETAIL SUBSCREEN)
// ============================================================================
export function renderSuperAndSharesScreen(ctx) {
  const { state, ui } = ctx;
  const cfg = state.appConfig;
  const hide = state.isWealthHidden;
  const sharesAccent = hexLongToCss(cfg.colorSuperSharesHex, "#38BDF8");
  const p1Color = hexLongToCss(cfg.person1ColorHex, "#E38391");
  const p2Color = hexLongToCss(cfg.person2ColorHex, "#34D399");

  const ownerFilter = ui.investOwnerFilter || "All";
  const ownerChoices = cfg.isCoupleMode ? ["All", cfg.person1Name, cfg.person2Name] : ["All", cfg.person1Name];

  const filteredShares = ownerFilter === "All"
    ? state.sharesPortfolio
    : state.sharesPortfolio.filter((s) => String(s.owner || "").toLowerCase() === ownerFilter.toLowerCase());
  const filteredSuper = ownerFilter === "All"
    ? state.superAccounts
    : state.superAccounts.filter((s) => String(s.owner || "").toLowerCase() === ownerFilter.toLowerCase());

  const totalSharesAll = state.sharesPortfolio.reduce((s, p) => s + Number(p.units || 0) * Number(p.currentPrice || 0), 0);
  const totalSuperAll = state.superAccounts.reduce((s, a) => s + Number(a.balance || 0), 0);

  // Share Detail Subscreen
  if (ui.selectedHoldingKey) {
    const allGrouped = groupShareHoldings(filteredShares);
    const holding = allGrouped.find((h) => h.holdingKey === ui.selectedHoldingKey) ||
      groupShareHoldings(state.sharesPortfolio).find((h) => h.holdingKey === ui.selectedHoldingKey);
    if (holding) {
      return renderShareDetailView(ctx, holding, sharesAccent, p1Color, p2Color);
    }
  }

  const activeSubTab = ui.investSubTab || "SHARES";
  const totalSharesVal = filteredShares.reduce((s, p) => s + Number(p.units || 0) * Number(p.currentPrice || 0), 0);
  const totalCostBasis = filteredShares.reduce((s, p) => s + Number(p.units || 0) * Number(p.purchasePrice || 0), 0);
  const totalGain = totalSharesVal - totalCostBasis;
  const totalGainPct = totalCostBasis > 0 ? (totalGain / totalCostBasis) * 100 : 0;
  const grouped = groupShareHoldings(filteredShares);
  const totalSuperVal = filteredSuper.reduce((s, a) => s + Number(a.balance || 0), 0);

  const timeframe = ui.shareTimeframe || "ALL";
  const syncLabel = state.lastShareSyncTime
    ? new Date(state.lastShareSyncTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
    : "Never";

  return `
    <div class="screen-stack">
      <!-- Category Switcher: Shares vs Superannuation -->
      <div class="grid-2">
        <button type="button" class="cat-select-card ${activeSubTab === "SHARES" ? "selected" : ""}" data-invest-sub="SHARES" style="${activeSubTab === "SHARES" ? `background:${sharesAccent};color:#04140E` : ""}">
          <div class="fw-bold">Shares Portfolio</div>
          <div class="text-xs">${state.sharesPortfolio.length} Parcels (ASX &amp; US)</div>
          <div class="stat-val-sm" style="margin-top:6px;">${formatCurrency(totalSharesAll, 0, hide)}</div>
        </button>
        <button type="button" class="cat-select-card ${activeSubTab === "SUPER" ? "selected" : ""}" data-invest-sub="SUPER" style="${activeSubTab === "SUPER" ? `background:#FBBF24;color:#04140E` : ""}">
          <div class="fw-bold">Superannuation</div>
          <div class="text-xs">${state.superAccounts.length} Funds</div>
          <div class="stat-val-sm" style="margin-top:6px;">${formatCurrency(totalSuperAll, 0, hide)}</div>
        </button>
      </div>

      <!-- Owner Filter + Sync Timestamp -->
      <div class="row-between" style="flex-wrap:wrap;gap:8px;">
        <div class="filter-chip-row">
          ${ownerChoices.map((o) => `<button type="button" class="filter-chip ${ownerFilter === o ? "active" : ""}" data-invest-owner="${esc(o)}">${esc(o)}</button>`).join("")}
        </div>
        ${activeSubTab === "SHARES" ? `<span class="text-xs c-dim fw-bold">Synced: ${esc(syncLabel)}</span>` : ""}
      </div>

      ${activeSubTab === "SHARES" ? `
        <div class="row" style="gap:8px;">
          <button type="button" class="btn btn-primary" style="flex:1;background:${sharesAccent};color:#04140E" data-action="add-share">+ Add Share</button>
          <button type="button" class="btn btn-outline" id="sync-shares-btn" ${ui.isSyncingShares ? "disabled" : ""}>
            ${ui.isSyncingShares ? "Syncing..." : "↻ Sync Prices"}
          </button>
        </div>

        <!-- Shares Holding Timeline Card -->
        <div class="glass-card">
          <div class="row-between" style="margin-bottom:8px;">
            <div>
              <div class="text-xs c-dim fw-bold">${esc(ownerFilter.toUpperCase())} PORTFOLIO VALUE</div>
              <div id="share-chart-val" class="stat-val">${formatCurrency(totalSharesVal, 2, hide)}</div>
              <div id="share-chart-sub" class="text-xs ${totalGain >= 0 ? "c-mint" : "c-coral"} fw-bold">
                ${hide ? "$ ••••••" : `${totalGain >= 0 ? "↗ +" : "↘ "}${formatCurrency(totalGain, 2)} (${totalGainPct.toFixed(1)}%) vs Cost Basis`}
              </div>
            </div>
            <div class="timeframe-pills">
              ${["1M", "3M", "6M", "1Y", "ALL"].map((tf) => `<button type="button" class="tf-pill ${timeframe === tf ? "active" : ""}" data-share-tf="${tf}">${tf}</button>`).join("")}
            </div>
          </div>
          <canvas id="shares-timeline-canvas" class="chart-scrub-canvas" style="height:150px;"></canvas>
          <div class="row-between text-xs c-dim" style="margin-top:4px;">
            <span>Drag across chart to inspect</span>
            <span>Dashed = Cost Basis</span>
          </div>
        </div>

        <!-- Cost Basis & All-Time Return Capsule -->
        <div class="dual-metric-capsule">
          <div>
            <span class="text-xs c-dim fw-bold">COST BASIS</span>
            <div class="fw-bold">${formatCurrency(totalCostBasis, 2, hide)}</div>
          </div>
          <div style="text-align:right;">
            <span class="text-xs c-dim fw-bold">ALL-TIME RETURN</span>
            <div class="fw-bold ${hide ? "" : (totalGain >= 0 ? "c-mint" : "c-coral")}">
              ${hide ? "$ ••••••" : `${totalGain >= 0 ? "+" : ""}${formatCurrency(totalGain, 2)} (${totalGainPct.toFixed(1)}%)`}
            </div>
          </div>
        </div>

        <div class="row-between">
          <span class="text-xs fw-bold c-dim">PORTFOLIO HOLDINGS (${grouped.length})</span>
          <span class="text-xs c-dim">Sorted by value</span>
        </div>

        ${grouped.length ? grouped.map((h) => {
          const isGain = h.totalGainLoss >= 0;
          const gainCls = isGain ? "c-mint" : "c-coral";
          const priceDiffPct = h.averagePurchasePrice > 0 ? ((h.currentPrice - h.averagePurchasePrice) / h.averagePurchasePrice) * 100 : 0;
          return `
            <div class="glass-card compact clickable-card" data-open-holding="${esc(h.holdingKey)}">
              <div class="row-between" style="align-items:flex-start;">
                <div class="row" style="gap:10px;flex:1;min-width:0;">
                  ${renderShareLogoBadge(h.code, h.market)}
                  <div style="min-width:0;">
                    <div class="row" style="gap:5px;flex-wrap:wrap;">
                      <span class="mini-badge fw-bold" style="color:${sharesAccent};background:${sharesAccent}22">${esc(h.code)}</span>
                      ${h.isUsMarket ? `<span class="mini-badge c-purple">US</span>` : ""}
                      ${h.owners.map((ow) => {
                        const col = ow.toLowerCase() === cfg.person1Name.toLowerCase() ? p1Color : p2Color;
                        return `<span class="mini-badge" style="color:${col};border-color:${col}66;background:${col}1F">${esc(ow)}</span>`;
                      }).join("")}
                      <span class="mini-badge">${h.parcels.length} ${h.parcels.length === 1 ? "buy" : "buys"}</span>
                    </div>
                    <div class="text-xs c-dim" style="margin-top:4px;">${formatShareUnits(h.totalUnits)} units held</div>
                  </div>
                </div>
                <div style="text-align:right;">
                  <div class="stat-val-sm">${formatCurrency(h.totalMarketValue, 2, hide)}</div>
                  <div class="text-xs fw-bold ${hide ? "" : gainCls}">
                    ${hide ? "$ ••••••" : `${isGain ? "↗ +" : "↘ "}${formatCurrency(h.totalGainLoss, 2)} (${h.gainLossPct.toFixed(1)}%)`}
                  </div>
                </div>
              </div>

              <div class="dual-metric-capsule" style="margin-top:10px;">
                <div>
                  <div class="text-xs c-dim fw-bold">${h.isUsMarket ? "AVG BUY (AUD)" : "AVG BUY PRICE"}</div>
                  <div class="fw-bold">${formatCurrency(h.averagePurchasePrice, 2, hide)}</div>
                  ${h.isUsMarket && h.usdAveragePurchasePrice > 0 && !hide ? `<div class="text-xs c-dim">US$${h.usdAveragePurchasePrice.toFixed(2)}</div>` : ""}
                </div>
                <div style="text-align:right;">
                  <div class="text-xs fw-bold" style="color:${sharesAccent}">${h.isUsMarket ? "CURRENT PRICE (AUD)" : "CURRENT ASX PRICE"}</div>
                  <div class="fw-bold" style="color:${sharesAccent}">
                    ${formatCurrency(h.currentPrice, 2, hide)}
                    ${!hide && h.averagePurchasePrice > 0 ? `<span class="text-xs ${priceDiffPct >= 0 ? "c-mint" : "c-coral"}"> (${priceDiffPct >= 0 ? "+" : ""}${priceDiffPct.toFixed(1)}%)</span>` : ""}
                  </div>
                  ${h.isUsMarket && h.usdCurrentPrice > 0 && !hide ? `<div class="text-xs c-dim">US$${h.usdCurrentPrice.toFixed(2)} USD</div>` : ""}
                </div>
              </div>

              <div class="row-between" style="margin-top:8px;">
                <span class="text-xs c-dim">Cost Basis: ${formatCurrency(h.totalCostBasis, 2, hide)}</span>
                <span class="text-xs fw-bold" style="color:${sharesAccent}">View Graph &amp; Buys ›</span>
              </div>
            </div>
          `;
        }).join("") : `
          <div class="glass-card" style="text-align:center;padding:28px 16px;">
            <div style="font-size:32px;">📈</div>
            <div class="card-title" style="margin-top:6px;">No shares tracked yet</div>
            <div class="text-xs c-dim" style="margin-top:4px;">Tap '+ Add Share' above to log your first ASX or US share purchase.</div>
          </div>
        `}
      ` : `
        <button type="button" class="btn btn-primary btn-block" style="background:#FBBF24;color:#04140E" data-action="add-super">+ Add Super Account</button>
        <div class="glass-card hero-card">
          <div class="card-kicker c-amber">${esc(ownerFilter.toUpperCase())} SUPERANNUATION TOTAL</div>
          <div class="hero-big-num">${formatCurrency(totalSuperVal, 0, hide)}</div>
        </div>
        ${filteredSuper.length ? filteredSuper.map((acc) => {
          const isP1 = String(acc.owner || "").toLowerCase() === cfg.person1Name.toLowerCase();
          const oCol = isP1 ? p1Color : p2Color;
          return `
            <div class="glass-card compact">
              <div class="item-row">
                <div class="item-left">
                  <div class="item-icon-circle" style="background:${oCol}26;color:${oCol}">🏛️</div>
                  <div class="item-text-col">
                    <div class="item-title">${esc(acc.fundName)}</div>
                    <span class="mini-badge" style="color:${oCol};border-color:${oCol}66;background:${oCol}1F">${esc(acc.owner)}</span>
                  </div>
                </div>
                <div class="item-right-col">
                  <strong>${formatCurrency(acc.balance, 0, hide)}</strong>
                  <div class="item-inline-actions">
                    <button type="button" class="mini-action-btn edit" data-action="edit-super" data-id="${esc(acc.id)}">✎</button>
                    <button type="button" class="mini-action-btn delete" data-action="delete-super" data-id="${esc(acc.id)}">🗑</button>
                  </div>
                </div>
              </div>
            </div>
          `;
        }).join("") : `<div class="glass-card"><div class="text-xs c-dim">No superannuation accounts added yet.</div></div>`}
      `}
    </div>
  `;
}

function renderShareDetailView(ctx, holding, sharesAccent, p1Color, p2Color) {
  const { state, ui } = ctx;
  const cfg = state.appConfig;
  const hide = state.isWealthHidden;
  const isGain = holding.totalGainLoss >= 0;
  const timeframe = ui.shareTimeframe || "ALL";

  return `
    <div class="screen-stack">
      <div class="row-between">
        <button type="button" class="chip-btn" id="close-holding-detail">← Back to Portfolio</button>
        <button type="button" class="btn btn-sm btn-primary" style="background:${sharesAccent};color:#04140E" data-action="add-share" data-ticker="${esc(holding.code)}" data-market="${esc(holding.market)}" data-cprice="${holding.currentPrice}">+ Add Buy</button>
      </div>

      <div class="glass-card hero-card">
        <div class="row" style="gap:12px;">
          ${renderShareLogoBadge(holding.code, holding.market)}
          <div style="flex:1;">
            <div class="row" style="gap:6px;">
              <strong style="font-size:1.25rem;">${esc(holding.code)}</strong>
              <span class="mini-badge">${holding.isUsMarket ? "US Market" : "ASX"}</span>
            </div>
            <div class="text-xs c-dim">${formatShareUnits(holding.totalUnits)} units • Avg Buy ${formatCurrency(holding.averagePurchasePrice, 2, hide)} • Current ${formatCurrency(holding.currentPrice, 2, hide)}</div>
          </div>
          <div style="text-align:right;">
            <div class="stat-val">${formatCurrency(holding.totalMarketValue, 2, hide)}</div>
            <div class="text-xs fw-bold ${isGain ? "c-mint" : "c-coral"}">${hide ? "$ ••••••" : `${isGain ? "+" : ""}${formatCurrency(holding.totalGainLoss, 2)} (${holding.gainLossPct.toFixed(1)}%)`}</div>
          </div>
        </div>
      </div>

      <div class="glass-card">
        <div class="row-between" style="margin-bottom:8px;">
          <div>
            <div class="text-xs c-dim fw-bold">${esc(holding.code)} PERFORMANCE</div>
            <div id="share-detail-val" class="stat-val-sm">${formatCurrency(holding.totalMarketValue, 2, hide)}</div>
            <div id="share-detail-sub" class="text-xs c-dim">Today</div>
          </div>
          <div class="timeframe-pills">
            ${["1M", "3M", "6M", "1Y", "ALL"].map((tf) => `<button type="button" class="tf-pill ${timeframe === tf ? "active" : ""}" data-share-tf="${tf}">${tf}</button>`).join("")}
          </div>
        </div>
        <canvas id="share-detail-canvas" class="chart-scrub-canvas" style="height:150px;"></canvas>
      </div>

      <div class="text-xs fw-bold c-dim">INDIVIDUAL PURCHASES (${holding.parcels.length})</div>
      ${holding.parcels.map((p) => {
        const pVal = Number(p.units || 0) * Number(p.currentPrice || 0);
        const pCost = Number(p.units || 0) * Number(p.purchasePrice || 0);
        const pGain = pVal - pCost;
        const oCol = String(p.owner || "").toLowerCase() === cfg.person1Name.toLowerCase() ? p1Color : p2Color;
        return `
          <div class="glass-card compact">
            <div class="row-between">
              <div>
                <div class="row" style="gap:6px;">
                  <strong>${esc(formatParcelDate(p))}</strong>
                  <span class="mini-badge" style="color:${oCol};border-color:${oCol}66">${esc(p.owner)}</span>
                </div>
                <div class="text-xs c-dim">${formatShareUnits(p.units)} units @ ${formatCurrency(p.purchasePrice, 2, hide)}</div>
              </div>
              <div class="item-right-col">
                <div>
                  <div class="fw-bold" style="text-align:right;">${formatCurrency(pVal, 2, hide)}</div>
                  <div class="text-xs ${pGain >= 0 ? "c-mint" : "c-coral"}" style="text-align:right;">${hide ? "$•••" : `${pGain >= 0 ? "+" : ""}${formatCurrency(pGain, 2)}`}</div>
                </div>
                <div class="item-inline-actions">
                  <button type="button" class="mini-action-btn edit" data-action="edit-share" data-id="${esc(p.id)}">✎</button>
                  <button type="button" class="mini-action-btn delete" data-action="delete-share" data-id="${esc(p.id)}">🗑</button>
                </div>
              </div>
            </div>
          </div>
        `;
      }).join("")}
    </div>
  `;
}

export function mountInvestCharts(ctx) {
  const { state, ui } = ctx;
  const ownerFilter = ui.investOwnerFilter || "All";
  const filteredShares = ownerFilter === "All"
    ? state.sharesPortfolio
    : state.sharesPortfolio.filter((s) => String(s.owner || "").toLowerCase() === ownerFilter.toLowerCase());
  const timeframe = ui.shareTimeframe || "ALL";
  const hide = state.isWealthHidden;

  // Main portfolio chart
  const mainCanvas = document.getElementById("shares-timeline-canvas");
  if (mainCanvas) {
    const pts = buildPurchaseAwareTimelinePoints(filteredShares, timeframe);
    const totalNow = filteredShares.reduce((s, p) => s + Number(p.units || 0) * Number(p.currentPrice || 0), 0);
    const totalCost = filteredShares.reduce((s, p) => s + Number(p.units || 0) * Number(p.purchasePrice || 0), 0);
    const gain = totalNow - totalCost;
    const lineColor = gain >= 0 ? "#34D399" : "#E38391";

    renderShareTimelineChart(mainCanvas, pts, { lineColor, showBuyMarkers: true });
    attachScrubHandlers(mainCanvas, pts, (idx) => {
      renderShareTimelineChart(mainCanvas, pts, { lineColor, scrubIdx: idx, showBuyMarkers: true });
      const valEl = document.getElementById("share-chart-val");
      const subEl = document.getElementById("share-chart-sub");
      if (!valEl || !subEl) return;
      if (idx === null || !pts[idx]) {
        const pct = totalCost > 0 ? (gain / totalCost) * 100 : 0;
        valEl.textContent = formatCurrency(totalNow, 2, hide);
        subEl.textContent = hide ? "$ ••••••" : `${gain >= 0 ? "↗ +" : "↘ "}${formatCurrency(gain, 2)} (${pct.toFixed(1)}%) vs Cost Basis`;
      } else {
        const pt = pts[idx];
        const ptGain = pt.value - pt.costBasisAtDate;
        valEl.textContent = formatCurrency(pt.value, 2, hide);
        subEl.textContent = `${pt.dateText} • Cost ${formatCurrency(pt.costBasisAtDate, 0, hide)} (${ptGain >= 0 ? "+" : ""}${formatCurrency(ptGain, 0, hide)})`;
      }
    });
  }

  // Detail chart
  const detailCanvas = document.getElementById("share-detail-canvas");
  if (detailCanvas && ui.selectedHoldingKey) {
    const holding = groupShareHoldings(filteredShares).find((h) => h.holdingKey === ui.selectedHoldingKey) ||
      groupShareHoldings(state.sharesPortfolio).find((h) => h.holdingKey === ui.selectedHoldingKey);
    if (holding) {
      const pts = buildPurchaseAwareTimelinePoints(holding.parcels, timeframe);
      const lineColor = holding.totalGainLoss >= 0 ? "#34D399" : "#E38391";
      renderShareTimelineChart(detailCanvas, pts, { lineColor, showBuyMarkers: false });
      attachScrubHandlers(detailCanvas, pts, (idx) => {
        renderShareTimelineChart(detailCanvas, pts, { lineColor, scrubIdx: idx, showBuyMarkers: false });
        const valEl = document.getElementById("share-detail-val");
        const subEl = document.getElementById("share-detail-sub");
        if (!valEl || !subEl) return;
        if (idx === null || !pts[idx]) {
          valEl.textContent = formatCurrency(holding.totalMarketValue, 2, hide);
          subEl.textContent = "Today";
        } else {
          valEl.textContent = formatCurrency(pts[idx].value, 2, hide);
          subEl.textContent = `${pts[idx].dateText} • Cost ${formatCurrency(pts[idx].costBasisAtDate, 0, hide)}`;
        }
      });
    }
  }
}

// ============================================================================
// 5. OTHER ASSETS SCREEN (WITH DIMINISHING VALUE DEPRECIATION)
// ============================================================================
export function renderOtherAssetsScreen(ctx) {
  const { state, ui } = ctx;
  const cfg = state.appConfig;
  const hide = state.isWealthHidden;
  const p1Color = hexLongToCss(cfg.person1ColorHex, "#E38391");
  const p2Color = hexLongToCss(cfg.person2ColorHex, "#34D399");

  const ownerFilter = ui.otherOwnerFilter || "All";
  const ownerChoices = cfg.isCoupleMode ? ["All", cfg.person1Name, cfg.person2Name] : ["All", cfg.person1Name];
  const forecastYrs = cfg.depreciateOtherAssets ? (ui.otherForecastYears || 0) : 0;

  const filtered = ownerFilter === "All"
    ? state.otherAssets
    : state.otherAssets.filter((a) => String(a.owner || "").toLowerCase() === ownerFilter.toLowerCase());

  const valFor = (asset) => projectedAssetValueAfterYears(asset, forecastYrs, cfg.depreciateOtherAssets);
  const totalVal = filtered.reduce((s, a) => s + valFor(a), 0);
  const totalOrig = filtered.reduce((s, a) => s + Number(a.value || 0), 0);
  const totalDelta = totalVal - totalOrig;
  const depCount = filtered.filter((a) => a.isDepreciating).length;
  const p1Tot = state.otherAssets.filter((a) => String(a.owner || "").toLowerCase() === cfg.person1Name.toLowerCase()).reduce((s, a) => s + valFor(a), 0);
  const p2Tot = state.otherAssets.filter((a) => String(a.owner || "").toLowerCase() === cfg.person2Name.toLowerCase()).reduce((s, a) => s + valFor(a), 0);

  return `
    <div class="screen-stack">
      <div class="row-between" style="flex-wrap:wrap;gap:8px;">
        <div class="filter-chip-row">
          ${ownerChoices.map((o) => `<button type="button" class="filter-chip ${ownerFilter === o ? "active" : ""}" data-other-owner="${esc(o)}">${esc(o)}</button>`).join("")}
        </div>
        <button type="button" class="btn btn-primary btn-sm" style="background:#E38391;color:#04140E" data-action="add-other-asset">+ Add Asset</button>
      </div>

      <div class="glass-card hero-card">
        <div class="row-between">
          <div>
            <div class="card-kicker c-coral">${forecastYrs > 0 ? `${esc(ownerFilter.toUpperCase())} ASSETS (+${forecastYrs}Y EST.)` : `${esc(ownerFilter.toUpperCase())} OTHER ASSETS TOTAL`}</div>
            <div class="hero-big-num">${formatCurrency(totalVal, 0, hide)}</div>
          </div>
          ${cfg.depreciateOtherAssets && depCount > 0 && totalDelta < -0.5 ? `
            <span class="pill-badge c-coral">${hide ? "••%" : `${((totalDelta / Math.max(1, totalOrig)) * 100).toFixed(1)}%`}</span>` : ""}
        </div>

        ${cfg.depreciateOtherAssets && depCount > 0 && totalDelta < -0.5 ? `
          <div class="text-xs c-dim" style="margin-top:4px;">Original: ${formatCurrency(totalOrig, 0, hide)} • Depreciated: ${formatCurrency(totalDelta, 0, hide)}</div>` : ""}

        <div class="divider"></div>

        <label class="toggle-line">
          <div>
            <div class="fw-bold" style="font-size:13.5px;">Depreciate Assets Over Time</div>
            <div class="text-xs c-dim">${cfg.depreciateOtherAssets ? `Active using diminishing value (${depCount} depreciating)` : "Off — showing original asset values"}</div>
          </div>
          <input type="checkbox" id="toggle-depreciate-assets" ${cfg.depreciateOtherAssets ? "checked" : ""} />
        </label>

        <div class="row-between" style="margin-top:8px;">
          <button type="button" class="chip-btn" data-action="show-avg-rates">ⓘ Avg. Market Rates</button>
          ${cfg.depreciateOtherAssets && filtered.some((a) => a.isDepreciating) ? `
            <div class="filter-chip-row">
              ${[[0, "Today"], [1, "+1 Yr"], [3, "+3 Yrs"], [5, "+5 Yrs"]].map(([y, lbl]) =>
                `<button type="button" class="filter-chip ${forecastYrs === y ? "active" : ""}" data-other-forecast="${y}">${lbl}</button>`
              ).join("")}
            </div>` : ""}
        </div>

        ${cfg.isCoupleMode ? `
          <div class="divider"></div>
          <div class="row-between">
            <div><div class="text-xs c-dim">${esc(cfg.person1Name)}'s Assets</div><strong style="color:${p1Color}">${formatCurrency(p1Tot, 0, hide)}</strong></div>
            <div style="text-align:right;"><div class="text-xs c-dim">${esc(cfg.person2Name)}'s Assets</div><strong style="color:${p2Color}">${formatCurrency(p2Tot, 0, hide)}</strong></div>
          </div>` : ""}
      </div>

      ${filtered.length ? filtered.map((asset) => {
        const typeObj = parseOtherAssetType(asset.assetType, asset.name);
        const isP1 = String(asset.owner || "").toLowerCase() === cfg.person1Name.toLowerCase();
        const oCol = isP1 ? p1Color : p2Color;
        const dispVal = valFor(asset);
        const delta = dispVal - Number(asset.value || 0);
        const activelyDep = cfg.depreciateOtherAssets && asset.isDepreciating && Number(asset.annualDepreciationRate || 0) > 0;
        const rateTxt = Number(asset.annualDepreciationRate || 0) % 1 === 0 ? `${Math.round(asset.annualDepreciationRate)}%` : `${Number(asset.annualDepreciationRate).toFixed(1)}%`;

        return `
          <div class="glass-card compact">
            <div class="item-row">
              <div class="item-left">
                <div class="item-icon-circle" style="background:${oCol}26;color:${oCol}">${typeObj.emoji}</div>
                <div class="item-text-col">
                  <div class="item-title">${esc(asset.name)}</div>
                  <div class="row" style="gap:5px;flex-wrap:wrap;margin-top:3px;">
                    <span class="mini-badge" style="color:${oCol};border-color:${oCol}66">${esc(asset.owner)}</span>
                    <span class="mini-badge">${esc(typeObj.shortLabel)}</span>
                    <button type="button" class="mini-badge clickable-badge" data-action="toggle-asset-dep" data-id="${esc(asset.id)}" style="${activelyDep ? "color:#FBBF24;border-color:#FBBF2466" : ""}">
                      ${activelyDep ? `-${rateTxt}/yr` : (asset.isDepreciating ? "Paused" : "Static")}
                    </button>
                  </div>
                </div>
              </div>
              <div class="item-right-col">
                <div>
                  <div class="fw-bold" style="text-align:right;">${formatCurrency(dispVal, 0, hide)}</div>
                  ${activelyDep && delta < -0.5 ? `<div class="text-xs c-coral" style="text-align:right;">${formatCurrency(delta, 0, hide)} vs orig</div>` : ""}
                </div>
                <div class="item-inline-actions">
                  <button type="button" class="mini-action-btn edit" data-action="edit-other-asset" data-id="${esc(asset.id)}">✎</button>
                  <button type="button" class="mini-action-btn delete" data-action="delete-other-asset" data-id="${esc(asset.id)}">🗑</button>
                </div>
              </div>
            </div>
          </div>
        `;
      }).join("") : `
        <div class="glass-card" style="text-align:center;padding:24px 16px;">
          <div style="font-size:32px;">🚗</div>
          <div class="card-title" style="margin-top:6px;">No Other Assets Added Yet</div>
          <div class="text-xs c-dim" style="margin-top:4px;">Add cars, tech, boats, furniture, or cash savings and automatically track depreciation by asset type.</div>
        </div>
      `}
    </div>
  `;
}
