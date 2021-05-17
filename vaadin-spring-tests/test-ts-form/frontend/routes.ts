import { Commands, Context, Route } from "@vaadin/router";
import "./views/main-view";

export type ViewRoute = Route & { title?: string; children?: ViewRoute[] };

export const views: ViewRoute[] = [
  // for client-side, place routes below (more info https://vaadin.com/docs/v19/flow/typescript/creating-routes.html)
  {
    path: "",
    component: "vaadin-elements-view",
    title: "Elements",
    action: async () => {
      await import("./views/form/vaadin-elements-view");
    },
  }
];
export const routes: ViewRoute[] = [
  {
    path: "",
    component: "main-view",
    children: [...views],
  },
];
