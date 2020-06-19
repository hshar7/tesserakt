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
import Button from "components/CustomButtons/Button.jsx";
import CustomInput from "components/CustomInput/CustomInput.jsx";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
import Snackbars from "components/Snackbar/Snackbar.jsx";

import { signup } from "APIUtils";

import registerPageStyle from "assets/jss/material-dashboard-pro-react/views/registerPageStyle";

class RegisterPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      email: "",
      registrationToken: "",
      username: "",
      orgName: "",
      password: "",
      badData: false,
      something_went_wrong: false,
      success: false
    };
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange = event => {
    this.setState({[event.target.id]: event.target.value});
  };

  handleSubmit(event) {
    event.preventDefault();
    signup({
      name: this.state.name,
      username: this.state.username,
      email: this.state.email,
      password: this.state.password,
      organizationName: this.state.orgName,
      signUpToken: this.state.registrationToken
    }).then(response => {
      this.setState({success: true});
    }).catch(error => {
        if (error.status === 401) {
          this.setState({badData: true});
        } else {
          this.setState({something_went_wrong: true});
        }
    });
  }

  render() {
    const { classes } = this.props;
    return (
      <div className={classes.container}>
        <GridContainer justify="center">
          <GridItem xs={12} sm={12} md={10}>
            <Card className={classes.cardSignup}>
              <h2 className={classes.cardTitle}>Register</h2>
              <CardBody>
                <GridContainer justify="center">
                  <GridItem xs={12} sm={8} md={5}>
                    <form className={classes.form} onSubmit={this.handleSubmit} >
                      <CustomInput
                        formControlProps={{
                          fullWidth: true,
                          className: classes.customFormControlClasses
                        }}
                        id="name"
                        inputProps={{
                          onChange: this.handleChange,
                          startAdornment: (
                            <InputAdornment
                              position="start"
                              className={classes.inputAdornment}
                            >
                              <Face className={classes.inputAdornmentIcon} />
                            </InputAdornment>
                          ),
                          placeholder: "Full Name..."
                        }}
                      />
                      <CustomInput
                        formControlProps={{
                          fullWidth: true,
                          className: classes.customFormControlClasses
                        }}
                        id="email"
                        inputProps={{
                          onChange: this.handleChange,
                          startAdornment: (
                            <InputAdornment
                              position="start"
                              className={classes.inputAdornment}
                            >
                              <Email className={classes.inputAdornmentIcon} />
                            </InputAdornment>
                          ),
                          placeholder: "Email..."
                        }}
                      />
                      <CustomInput
                        formControlProps={{
                          fullWidth: true,
                          className: classes.customFormControlClasses
                        }}
                        id="registrationToken"
                        inputProps={{
                          onChange: this.handleChange,
                          startAdornment: (
                            <InputAdornment
                              position="start"
                              className={classes.inputAdornment}
                            >
                              <Icon className={classes.inputAdornmentIcon}>
                                vpn_key
                              </Icon>
                            </InputAdornment>
                          ),
                          placeholder: "Registration Token..."
                        }}
                      />
                      <CustomInput
                        formControlProps={{
                          fullWidth: true,
                          className: classes.customFormControlClasses
                        }}
                        id="username"
                        inputProps={{
                          onChange: this.handleChange,
                          startAdornment: (
                            <InputAdornment
                              position="start"
                              className={classes.inputAdornment}
                            >
                              <Icon className={classes.inputAdornmentIcon}>
                                alternate_email
                              </Icon>
                            </InputAdornment>
                          ),
                          placeholder: "Username..."
                        }}
                      />
                      <CustomInput
                        formControlProps={{
                          fullWidth: true,
                          className: classes.customFormControlClasses
                        }}
                        id="orgName"
                        inputProps={{
                          onChange: this.handleChange,
                          startAdornment: (
                            <InputAdornment
                              position="start"
                              className={classes.inputAdornment}
                            >
                              <Icon className={classes.inputAdornmentIcon}>
                                business
                              </Icon>                            
                            </InputAdornment>
                          ),
                          placeholder: "Organization Name..."
                        }}
                      />
                      <CustomInput
                        formControlProps={{
                          fullWidth: true,
                          className: classes.customFormControlClasses
                        }}
                        id="password"
                        inputProps={{
                          onChange: this.handleChange,
                          type: "password",
                          startAdornment: (
                            <InputAdornment
                              position="start"
                              className={classes.inputAdornment}
                            >
                              <Icon className={classes.inputAdornmentIcon}>
                                lock_outline
                              </Icon>
                            </InputAdornment>
                          ),
                          placeholder: "Password..."
                        }}
                      />
                      <br />
                      <br />
                      <br />
                      <br />
                      <div className={classes.center}>
                        <Button round color="primary" type="submit">
                          Submit
                        </Button>
                      </div>
                    </form>
                  </GridItem>
                </GridContainer>
              </CardBody>
            </Card>
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
              open={this.state.something_went_wrong}
              closeNotification={() => this.setState({ something_went_wrong: false })}
            />
        </div>
        <div>
            <br />
            <Snackbars
              place="tl"
              color="success"
              message={'Registration Successful! Please Login!'}
              close
              open={this.state.success}
              closeNotification={() => this.setState({ success: false })}
            />
        </div>
      </div>
    );
  }
}

RegisterPage.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(registerPageStyle)(RegisterPage);
