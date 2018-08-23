import Pages from "layouts/Pages.jsx";
import Dashboard from "layouts/Dashboard.jsx";
import UserProfile from "../views/Components/UserProfile";

var indexRoutes = [
  { path: "/pages", name: "Pages", component: Pages },
  { path: "/", name: "Home", component: Dashboard },
];

export default indexRoutes;
