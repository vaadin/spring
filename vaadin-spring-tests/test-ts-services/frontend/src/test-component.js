import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
import * as appEndpoint from '../generated/AppEndpoint';

class TestComponent extends PolymerElement {
  static get template() {
    return html`
        <button id="button">Click</button>
        <button id="connect" on-click="connect">Click</button>
        <button id="connectAnonymous" on-click="connectAnonymous">Click anonymous</button>
        <button id="echoWithOptional" on-click="echoWithOptional">Echo with optional</button>
        <button id="helloAdmin" on-click="helloAdmin">Echo only admin</button>
        <div id="content"></div>
    `;
  }

  static get is() {
    return 'test-component'
  }

  connect(e) {
    appEndpoint
      .hello('Friend')
      .then(response => this.$.content.textContent = response)
      .catch(error => this.$.content.textContent = 'Error:' + error);
  }

  connectAnonymous(e) {
      appEndpoint
        .helloAnonymous()
        .then(response => this.$.content.textContent = response)
        .catch(error => this.$.content.textContent = 'Error:' + error);
    }

  echoWithOptional(e) {
    appEndpoint
      .echoWithOptional('one', undefined, 'three', 'four')
      .then(response => this.$.content.textContent = response)
      .catch(error => this.$.content.textContent = 'Error:' + error);
  }

  helloAdmin(e) {
    appEndpoint
      .helloAdmin()
      .then(response => this.$.content.textContent = response)
      .catch(error => this.$.content.textContent = 'Error:' + error);
  }
}
customElements.define(TestComponent.is, TestComponent);
