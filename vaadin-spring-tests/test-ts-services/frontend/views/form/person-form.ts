import { customElement, html, LitElement } from 'lit-element';

import { EndpointError } from '@vaadin/flow-frontend/Connect';
import * as PersonEndpoint from '../../generated/PersonEndpoint';
import { Binder, field } from '@vaadin/form';

import PersonModel from '../../generated/com/vaadin/flow/connect/data/entity/PersonModel';

@customElement('person-form')
export class PersonForm extends LitElement {

  private binder = new Binder(this, PersonModel);

  render() {
    return html`
      <h3>Personal information</h3>
        <input id="first-name" type="text" label="First name" ...="${field(this.binder.model.firstName)}">
        <input type="text" label="Last name" ...="${field(this.binder.model.lastName)}">
        <input type="text" label="Email address" ...="${field(this.binder.model.email)}">
        <input type="text" label="Occupation" ...="${field(this.binder.model.occupation)}">
        <button id="save" @click="${this.save}">
          Save
        </button>
        <button @click="${this.clearForm}">
          Cancel
        </button>
    `;
  }

  private async save() {
    try {
      const result = await this.binder.submitTo(PersonEndpoint.update);
      this.clearForm();
      alert('Person details stored ' + (result.isDeferred ? 'offline' : 'online'));
    } catch (error) {
      if (error instanceof EndpointError) {
        alert('Server error. ' + error.message);
      } else {
        throw error;
      }
    }
  }

  private clearForm() {
    this.binder.clear();
  }
}
