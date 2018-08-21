import React from "react";
import PropTypes from "prop-types";

// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import InputAdornment from "@material-ui/core/InputAdornment";
import Icon from "@material-ui/core/Icon";

// @material-ui/icons
import Face from "@material-ui/icons/Face";
import Email from "@material-ui/icons/Email";
// import LockOutline from "@material-ui/icons/LockOutline";

// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import CustomInput from "components/CustomInput/CustomInput.jsx";
import Button from "components/CustomButtons/Button.jsx";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
import CardHeader from "components/Card/CardHeader.jsx";
import CardFooter from "components/Card/CardFooter.jsx";

import loginPageStyle from "assets/jss/material-dashboard-pro-react/views/loginPageStyle.jsx";
import SnackbarContent from "components/Snackbar/SnackbarContent.jsx";
import { ACCESS_TOKEN } from '../../constants';
import { login } from '../../APIUtils';
import AddAlert from "@material-ui/icons/AddAlert";
import Snackbars from "components/Snackbar/Snackbar.jsx";
import { Redirect } from 'react-router-dom'

class LoginPage extends React.Component {
  constructor(props) {
    super(props);
    // we use this to make the card to appear after the page has been rendered
    this.state = {
      cardAnimaton: "cardHidden"
    };
    this.state = {
      usernameOrEmail: "",
      password: "",
      badData: false,
      something_went_wrong: false,
      redirect: false
    };
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange = event => {
    this.setState({[event.target.id]: event.target.value});
  };

  setRedirect = () => {
    this.setState({
      redirect: true
    })
  }

  renderRedirect = () => {
    if (this.state.redirect) {
      return <Redirect to='/dashboard' />
    }
  }

  handleSubmit(event) {
    event.preventDefault();
    login(this.state).then(response => {
        localStorage.setItem(ACCESS_TOKEN, response.accessToken);
        this.setRedirect();
    }).catch(error => {
        if(error.status === 401) {
          this.setState({['badData']: true});
        } else {
          this.setState({['something_went_wrong']: true});
        }
    });
  }
  
  componentDidMount() {
    // we add a hidden class to the card and after 700 ms we delete it and the transition appears
    this.timeOutFunction = setTimeout(
      function() {
        this.setState({ cardAnimaton: "" });
      }.bind(this),
      700
    );
  }
  componentWillUnmount(){
    clearTimeout(this.timeOutFunction);
    this.timeOutFunction = null;
  }
  render() {
    const { classes } = this.props;
    return (
      <div className={classes.container}>
        <GridContainer justify="center">
          <GridItem xs={12} sm={6} md={4}>
            <form onSubmit={this.handleSubmit}>
              <Card login className={classes[this.state.cardAnimaton]}>
                <CardBody>
                  <CustomInput
                    labelText="Username or Email.."
                    id="usernameOrEmail"
                    formControlProps={{
                      fullWidth: true
                    }}
                    inputProps={{
                      value: this.state.usernameOrEmail,
                      onChange: this.handleChange,
                      endAdornment: (
                        <InputAdornment position="end">
                          <Face className={classes.inputAdornmentIcon} />
                        </InputAdornment>
                      )
                    }}
                  />
                  <CustomInput
                    labelText="Password"
                    id="password"
                    formControlProps={{
                      fullWidth: true
                    }}
                    inputProps={{
                      value: this.state.password,
                      onChange: this.handleChange,
                      type: "password",
                      endAdornment: (
                        <InputAdornment position="end">
                          <Icon className={classes.inputAdornmentIcon}>
                            lock_outline
                          </Icon>
                        </InputAdornment>
                      )
                    }}
                  />
                </CardBody>
                <CardFooter className={classes.justifyContentCenter}>
                  <Button type="primary" htmlType="submit" color="rose" simple size="lg" block>
                    Login
                  </Button>
                </CardFooter>
              </Card>
            </form>
          </GridItem>
        </GridContainer>
        <div>
            <br />
            <Snackbars
              place="tl"
              color="danger"
              message={'Your Username or Password is incorrect. Please try again!'}
              close
              open={this.state.badData}
              closeNotification={() => this.setState({ badData: false })}
            />
            <br />
        </div>
        <div>
            <br />
            <Snackbars
              place="tl"
              color="danger"
              message={'Sorry! Something went wrong. Please try again!'}
              close
              open={this.state.somethingWentWrong}
              closeNotification={() => this.setState({ somethingWentWrong: false })}
            />
        </div>
        {this.renderRedirect()}
      </div>
      
    );
  }
}

LoginPage.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(loginPageStyle)(LoginPage);
