import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/app-layout/src/vaadin-drawer-toggle.js';
import '@vaadin/side-nav/src/vaadin-side-nav.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/side-nav/src/vaadin-side-nav-item.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/button/src/vaadin-button.js';
import 'Frontend/generated/jar-resources/buttonFunctions.js';
import '@vaadin/scroller/src/vaadin-scroller.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/app-layout/src/vaadin-app-layout.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '69ec6df9c7b1fe0840a1f0e5f75af2dd6eb7c0d21e25747149c934cb6c34fb8a') {
    pending.push(import('./chunks/chunk-bb56e1d9a2de44afb523d5f36560509f5b0e51abd341cf25f0ea5ea5994b4a2a.js'));
  }
  if (key === 'eb683806e4a6e8dbcd44c0f849378ea8b0ffa912086a848fbc2afe904099378d') {
    pending.push(import('./chunks/chunk-221441ed62830da65ba30c4430c20af887cb4f930386c3dc7c78e2e9cf97bac8.js'));
  }
  if (key === '48693bec7857a3613a4e6931f3e6fd6b7baa443cedc5948ddf5e4f58a7d057b3') {
    pending.push(import('./chunks/chunk-d47060096a7cc9d2d44a1726d1a4fecc2beffef9902ba8a71e2da06f5c0b73ba.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}