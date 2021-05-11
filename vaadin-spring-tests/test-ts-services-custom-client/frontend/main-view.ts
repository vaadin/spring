import { css, customElement, html, LitElement, property } from 'lit-element';

import * as appEndpoint from './generated/AppEndpoint';
import {AppEndpoint} from "./generated/AppEndpoint";

@customElement('main-view')
export class MainView extends LitElement {

  @property()
  private content?: string;

  render() {
    return html`
      <button id="helloAnonymous" @click="${this.helloAnonymous}">endpoint helloAnonymous</button><br/>
      <button id="helloAnonymousWrapper" @click="${this.helloAnonymousWrapper}">endpoint AppEndpoint.helloAnonymous</button><br/>
      <div id="content">${this.content}</div>
    `;
  }

  async helloAnonymous() {
    try {
      this.content = await appEndpoint.helloAnonymous();
    } catch (error) {
      this.content = 'Error:' + error;
    }
  }

  async helloAnonymousWrapper() {
    try {
      this.content = await AppEndpoint.helloAnonymous();
    } catch (error) {
      this.content = 'Error:' + error;
    }
  }

  static get styles() {
    return [
      css`
        :host {
          display: block;
          height: 100%;
        }
      `,
    ];
  }
}

