import Dashboard from "views/Dashboard/Dashboard.jsx";
import Buttons from "views/Components/Buttons.jsx";
import GridSystem from "views/Components/GridSystem.jsx";
import UserProfile from "views/Components/UserProfile.jsx";
import SweetAlert from "views/Components/SweetAlert.jsx";
import Notifications from "views/Components/Notifications.jsx";
import Icons from "views/Components/Icons.jsx";
import Typography from "views/Components/Typography.jsx";
import RegularForms from "views/Forms/RegularForms.jsx";
import ExtendedForms from "views/Forms/ExtendedForms.jsx";
import ValidationForms from "views/Forms/ValidationForms.jsx";
import Wizard from "views/Forms/Wizard.jsx";
import RegularTables from "views/Tables/RegularTables.jsx";
import ExtendedTables from "views/Tables/ExtendedTables.jsx";
import ReactTables from "views/Tables/ReactTables.jsx";
import GoogleMaps from "views/Maps/GoogleMaps.jsx";
import FullScreenMap from "views/Maps/FullScreenMap.jsx";
import VectorMap from "views/Maps/VectorMap.jsx";
import Charts from "views/Charts/Charts.jsx";
import Calendar from "views/Calendar/Calendar.jsx";
import Widgets from "views/Widgets/Widgets.jsx";
import TimelinePage from "views/Pages/Timeline.jsx";
import RTLSupport from "views/Pages/RTLSupport.jsx";

import pagesRoutes from "./pages.jsx";

// @material-ui/icons
import DashboardIcon from "@material-ui/icons/Dashboard";
import Image from "@material-ui/icons/Image";
import Apps from "@material-ui/icons/Apps";
// import ContentPaste from "@material-ui/icons/ContentPaste";
import GridOn from "@material-ui/icons/GridOn";
import Place from "@material-ui/icons/Place";
import WidgetsIcon from "@material-ui/icons/Widgets";
import Timeline from "@material-ui/icons/Timeline";
import DateRange from "@material-ui/icons/DateRange";

var dashRoutes = [
  {
    path: "/dashboard",
    name: "Portfolio",
    icon: DashboardIcon,
    component: Dashboard
  },
  {
    path: "/market",
    name: "Market",
    icon: DashboardIcon,
    component: Dashboard
  },
  {
    collapse: true,
    path: "/issuance",
    name: "Issuance",
    state: "openComponents",
    icon: Apps,
    views: [
      {
        path: "/issuance/new",
        name: "Launch Deal",
        mini: "B",
        component: Buttons
      },
      {
        path: "/issuance/view",
        name: "Live Deals",
        mini: "GS",
        component: GridSystem
      }
    ]
  },
  {
    path: "/settlements",
    name: "Settlements",
    icon: DashboardIcon,
    component: Dashboard
  },
  {
    path: "/syndicates",
    name: "Syndicates",
    icon: DashboardIcon,
    component: Dashboard
  },
  {
    path: "/settings",
    name: "Settings",
    icon: DashboardIcon,
    component: Dashboard
  },
  { redirect: true, path: "/", pathTo: "/pages/login-page", name: "Login" }
];
export default dashRoutes;
