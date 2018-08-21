// @material-ui/icons
import Dashboard from "@material-ui/icons/Dashboard";
import Person from "@material-ui/icons/Person";
// import ContentPaste from "@material-ui/icons/ContentPaste";
import LibraryBooks from "@material-ui/icons/LibraryBooks";
import BubbleChart from "@material-ui/icons/BubbleChart";
import LocationOn from "@material-ui/icons/LocationOn";
import Notifications from "@material-ui/icons/Notifications";
import Unarchive from "@material-ui/icons/Unarchive";
// core components/views
import DashboardPage from "views/Dashboard/Dashboard.jsx";
import UserProfile from "views/UserProfile/UserProfile.jsx";
import TableList from "views/TableList/TableList.jsx";
import Typography from "views/Typography/Typography.jsx";
import Icons from "views/Icons/Icons.jsx";
import Maps from "views/Maps/Maps.jsx";
import NotificationsPage from "views/Notifications/Notifications.jsx";
import UpgradeToPro from "views/UpgradeToPro/UpgradeToPro.jsx";

const dashboardRoutes = [
  {
    path: "/dashboard",
    sidebarName: "Portfolio",
    navbarName: "Portfolio Dashboard",
    icon: Dashboard,
    component: DashboardPage
  },
  {
    path: "/Market",
    sidebarName: "Market",
    navbarName: "Market",
    icon: "Business",
    component: DashboardPage
  },
  {
    path: "/newLoan",
    sidebarName: "Launch Deal",
    navbarName: "Launch Deal",
    icon: "Present_to_all",
    component: DashboardPage
  },
  {
    path: "/loans",
    sidebarName: "Live Deals",
    navbarName: "Live Deals",
    icon: "List",
    component: DashboardPage
  },
  {
    path: "/settlements",
    sidebarName: "Settelements",
    navbarName: "Settelements",
    icon: "Attach_money",
    component: DashboardPage
  },
  {
    path: "/syndicates",
    sidebarName: "Syndicates",
    navbarName: "Syndicates",
    icon: "People",
    component: DashboardPage
  },
  {
    path: "/notifications",
    sidebarName: "Notifications",
    navbarName: "Notifications",
    icon: Notifications,
    component: NotificationsPage
  },
  { redirect: true, path: "/", to: "/dashboard", navbarName: "Redirect" }
];

export default dashboardRoutes;
