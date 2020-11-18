import { customElement, html, internalProperty, LitElement } from 'lit-element';

// import the remote endpoint
import * as PersonEndpoint from '../../generated/PersonEndpoint';

import Person from '../../generated/com/vaadin/flow/connect/data/entity/Person';

@customElement('person-list')
export class PersonList extends LitElement {

  @internalProperty()
  private data: Person[] = [];

  render() {
    return html`
      <button id="delete-all" @click="${this.deleteAll}">
          Delete All
      </button>
      ${this.data.map((person:Person) => html`<li>${person.firstName}</li>`)}
    `;
  }

  // Wait until all elements in the template are ready to set their properties
  async firstUpdated() {
    this.data = await PersonEndpoint.list();
  }

  private async deleteAll() {
    await PersonEndpoint.deleteAll();
  }
}
