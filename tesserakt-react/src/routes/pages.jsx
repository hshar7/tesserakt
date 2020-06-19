import LoginPage from "views/Pages/LoginPage.jsx";
import Fingerprint from "@material-ui/icons/Fingerprint";
import AccountBox from "@material-ui/icons/AccountBox";
import RegisterPage from "../views/Pages/RegisterPage";

const pagesRoutes = [
  {
    path: "/pages/login-page",
    name: "Login Page",
    short: "Login",
    mini: "LP",
    icon: Fingerprint,
    component: LoginPage
  },
  {
    path: "/pages/register",
    name: "Registration",
    short: "Registration",
    mini: "R",
    icon: AccountBox,
    component: RegisterPage

  },
  {
    redirect: true,
    path: "/pages",
    pathTo: "/pages/login-page",
    name: "Login Page"
  }
];

export default pagesRoutes;
