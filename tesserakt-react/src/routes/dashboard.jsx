import Apps from "@material-ui/icons/Apps";
import ShoppingCart from "@material-ui/icons/ShoppingCart";
// @material-ui/icons
import DashboardIcon from "@material-ui/icons/Dashboard";
import GroupWorks from "@material-ui/icons/GroupWork";
import Dashboard from "views/Dashboard/Dashboard.jsx";
import NewDealForm from "views/Forms/NewDealForm.jsx";
import DealsSubscribedTable from "views/Tables/DealsSubscribedTable.jsx";
import NewMatchingCriteriaForm from "../views/Forms/NewMatchingCriteriaForm";
import Market from "../views/Tables/Market";
import DealRequest from "../views/DealRequest";
import MyMatchingCriteria from "../views/Tables/MyMatchingCriteria";
import Syndicates from "../views/Tables/Syndicates";
import DealRoom from "../views/DealRoom";
import ManageDeals from "../views/Tables/ManageDeals";
import RateDeals from "../views/Tables/RateDeals";
import ManageUsers from "../views/Tables/ManageUsers";

var dashRoutes = [
  {
    path: "/dashboard",
    name: "Portfolio",
    icon: DashboardIcon,
    component: Dashboard,
    roles: ["ROLE_UNDERWRITER", "ROLE_LENDER"]
  },
  {
    path: "/market/:id",
    name: "Market Deal",
    icon: ShoppingCart,
    component: DealRequest,
    roles: ["ROLE_UNDERWRITER", "ROLE_LENDER"]
  },
  {
    path: "/market",
    name: "Market",
    icon: ShoppingCart,
    component: Market,
    roles: ["ROLE_LENDER"]
  },
  {
    collapse: true,
    path: "/issuance",
    name: "Issuance",
    state: "openComponents",
    icon: Apps,
    roles: ["ROLE_UNDERWRITER", "ROLE_LENDER"],
    views: [
      {
        path: "/issuance/newDeal",
        name: "Launch Deal",
        mini: "LD",
        component: NewDealForm,
        roles: ["ROLE_UNDERWRITER"]
      },
      {
        path: "/issuance/view/:id",
        name: "Live Deals",
        mini: "LD",
        component: DealRoom,
        roles: ["ROLE_UNDERWRITER", "ROLE_LENDER"]
      },
      {
        path: "/issuance/view",
        name: "Live Deals",
        mini: "LD",
        component: DealsSubscribedTable,
        roles: ["ROLE_UNDERWRITER", "ROLE_LENDER"]
      },
      {
        path: "/issuance/newMatchingCriteria",
        name: "Create Matching Criteria",
        mini: "MC",
        component: NewMatchingCriteriaForm,
        roles: ["ROLE_LENDER"]
      },
      {
        path: "/issuance/matchingCriteria",
        name: "My Matching Criteria",
        mini: "MMC",
        component: MyMatchingCriteria,
        roles: ["ROLE_LENDER"]
      }
    ]
  },
  {
    path: "/settlements",
    name: "Settlements",
    icon: DashboardIcon,
    component: Dashboard,
    roles: ["ROLE_UNDERWRITER", "ROLE_LENDER"]
  },
  {
    path: "/syndicates",
    name: "Syndicates",
    icon: GroupWorks,
    component: Syndicates,
    roles: ["ROLE_UNDERWRITER", "ROLE_LENDER"]
  },
  {
    path: "/settings",
    name: "Settings",
    icon: DashboardIcon,
    component: Dashboard,
    roles: ["ROLE_UNDERWRITER", "ROLE_LENDER", "ROLE_ADMIN", "ROLE_RATING_AGENCY"]
  },
  {
    path: "/manageDeals",
    name: "Manage Deals",
    icon: Apps,
    component: ManageDeals,
    roles: ["ROLE_ADMIN"]
  },
  {
    path: "/manageUsers",
    name: "Manage Users",
    icon: GroupWorks,
    component: ManageUsers,
    roles: ["ROLE_ADMIN"]
  },
  {
    path: "/rateDeals",
    name: "Rate Deals",
    icon: Apps,
    component: RateDeals,
    roles: ["ROLE_RATING_AGENCY"]
  },
  { redirect: true, path: "/", pathTo: "/pages/login-page", name: "Login" }
];

export default dashRoutes;
