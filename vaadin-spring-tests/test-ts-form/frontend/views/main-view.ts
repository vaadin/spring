import { Router } from "@vaadin/router";
import "@vaadin/vaadin-app-layout";
import "@vaadin/vaadin-app-layout/vaadin-drawer-toggle";
import "@vaadin/vaadin-avatar/vaadin-avatar";
import "@vaadin/vaadin-tabs";
import "@vaadin/vaadin-tabs/vaadin-tab";
import { customElement, html } from "lit-element";
import { nothing } from "lit-html";
import { router } from "../index";
import { Layout } from "./view";

interface RouteInfo {
  path: string;
  title: string;
  requiresAuthentication?: boolean;
  requiresRole?: string;
  disable?: boolean;
}
@customElement("main-view")
export class MainView extends Layout {
  render() {
    return html`
      <vaadin-app-layout primary-section="drawer">
        <header slot="navbar" theme="dark">
          <vaadin-drawer-toggle></vaadin-drawer-toggle>
        </header>

        <div slot="drawer">
          <div id="logo">
          </div>
          <hr />
          <vaadin-tabs
            orientation="vertical"
            theme="minimal"
          >
            ${this.getMenuRoutes().map(
              (viewRoute) => html`
                <vaadin-tab>
                  <a href="${viewRoute.path}" tabindex="-1"
                    >${viewRoute.title}${viewRoute.disable
                      ? html` (hidden)`
                      : nothing}</a
                  >
                </vaadin-tab>
              `
            )}
          </vaadin-tabs>
        </div>
        <slot></slot>
      </vaadin-app-layout>
    `;
  }

  private getMenuRoutes(): RouteInfo[] {
    const views: RouteInfo[] = [
      {
        path: "",
        title: "Form",
      }
    ];
    return views;
  }
  connectedCallback() {
    super.connectedCallback();
    this.id = "main-view";
  }
}
