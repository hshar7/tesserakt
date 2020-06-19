import FormControl from "@material-ui/core/FormControl";
import FormLabel from "@material-ui/core/FormLabel";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import Select from "@material-ui/core/Select";
// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import newDealFormStyle from "assets/jss/material-dashboard-pro-react/views/newDealFormStyle";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
import CardHeader from "components/Card/CardHeader.jsx";
import CardText from "components/Card/CardText.jsx";
import Button from "components/CustomButtons/Button.jsx";
import CustomInput from "components/CustomInput/CustomInput.jsx";
import InputAdornment from '@material-ui/core/InputAdornment';
// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import React from "react";
import { Redirect } from 'react-router-dom';
// API
import { newDeal } from "../../APIUtils";

class NewDealForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      checked: [24, 22],
      selectedEnabled: "b",
      jurisdiction: "US",
      loanType: "Term",
      capitalAmount: "",
      interestRate: "",
      maturity: "",
      responseId: "",
      syndicateName: "",
      borrowerName: "",
      borrowerDescription: "",
      underwriterAmount: ""
    };
    this.handleSubmit = this.handleSubmit.bind(this);
  }
  handleSimple = event => {
    this.setState({ [event.target.name]: event.target.value });
  };
  renderRedirect = () => {
    if (this.state.redirect) {
      return <Redirect to={'/market/' + this.state.responseId} />
    }
  }

  handleSubmit(event) {
    event.preventDefault();
    newDeal({
      jurisdiction: this.state.jurisdiction,
      capitalAmount: this.state.capitalAmount,
      interestRate: this.state.interestRate,
      loanType: this.state.loanType,
      maturity: this.state.maturity,
      syndicateName: this.state.syndicateName,
      borrowerName: this.state.borrowerName,
      borrowerDescription: this.state.borrowerDescription,
      underwriterAmount: this.state.underwriterAmount
    }).then(response => {
      this.setState({responseId: response['id']});
      this.setState({redirect: true});
    }).catch(error => {
      // TODO: Give proper notifications in case of errors.
        // if(error.status === 401) {
        //   this.setState({['badData']: true});
        // } else {
        //   this.setState({['something_went_wrong']: true});
        // }
    });
  }

  render() {
    const { classes } = this.props;
    return (
      <GridContainer>
        <GridItem xs={12} sm={12} md={12}>
          <Card>
            <CardHeader color="rose" text>
              <CardText color="rose">
                <h5 className={classes.cardTitle}>Issue a new deal to the Market</h5>
              </CardText>
            </CardHeader>
            <CardBody>
              <form onSubmit={this.handleSubmit}>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Governing Jurisdiction
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={6} md={5} lg={5}>
                    <FormControl
                      fullWidth
                      className={classes.selectFormControl}
                    >
                      <InputLabel
                        htmlFor="jurisdiction"
                        className={classes.selectLabel}
                      >
                        Select Jurisdiction
                      </InputLabel>
                      <Select
                        MenuProps={{
                          className: classes.selectMenu
                      }}
                        classes={{
                          select: classes.select
                        }}
                        value={this.state.jurisdiction}
                        onChange={this.handleSimple}
                        inputProps={{
                            name: "jurisdiction",
                            id: "jurisdiction"
                        }}
                      >
                        <MenuItem
                          classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                      }}
                          value="US"
                        >
                          US
                        </MenuItem>
                        <MenuItem
                            classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                            }}
                          value="UK"
                        >
                          UK
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Syndicate Name
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: false
                      }}
                      inputProps={{
                        name: "syndicateName",
                        type: "text",
                        onChange: this.handleSimple,
                        required: true,
                        autoFocus: true
                      }}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Borrower Name
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: false
                      }}
                      inputProps={{
                        name: "borrowerName",
                        type: "text",
                        onChange: this.handleSimple,
                        required: true
                      }}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Borrower Description
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: true
                      }}
                      inputProps={{
                        name: "borrowerDescription",
                        type: "text",
                        onChange: this.handleSimple,
                        required: true
                      }}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Capital Amount
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: false
                      }}
                      inputProps={{
                        name: "capitalAmount",
                        type: "number",
                        onChange: this.handleSimple,
                        required: true,
                        startAdornment: <InputAdornment position="start">$</InputAdornment>
                      }}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      My Contribution
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: false
                      }}
                      inputProps={{
                        name: "underwriterAmount",
                        type: "number",
                        onChange: this.handleSimple,
                        required: true,
                        startAdornment: <InputAdornment position="start">$</InputAdornment>
                      }}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      InterestRate
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: false
                      }}
                      inputProps={{
                        name: "interestRate",
                        type: "text",
                        onChange:this.handleSimple,
                        required: true,
                        step: "any",
                        min: 0,
                        startAdornment: <InputAdornment position="start">%</InputAdornment>
                      }}
                      onChange={this.handleChange}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Loan Type
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={6} md={5} lg={5}>
                    <FormControl
                      fullWidth
                      className={classes.selectFormControl}
                    >
                      <InputLabel
                        htmlFor="loanType"
                        className={classes.selectLabel}
                      >
                        Select Type
                      </InputLabel>
                      <Select
                        MenuProps={{
                          className: classes.selectMenu
                      }}
                        classes={{
                          select: classes.select
                        }}
                        value={this.state.loanType}
                        onChange={this.handleSimple}
                        inputProps={{
                            name: "loanType",
                            id: "loanType",
                            required: true
                        }}
                      >
                      <MenuItem
                          classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                          }}
                          value="Term"
                        >
                          Term
                        </MenuItem>
                        <MenuItem
                          classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                          }}
                          value="Revolver"
                        >
                          Revolver
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Maturity
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: false
                      }}
                      inputProps={{
                        name: 'maturity',
                        type: 'number',
                        onChange:this.handleSimple,
                        required: true
                      }}
                      helpText="Days"
                      onChange={this.handleChange}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer justify="flex-end">
                  <GridItem xs={12} sm={12} md={9}>
                  <Button type="primary" htmlType="submit" color="rose" simple size="lg" block>
                  Submit
                  </Button>
                  </GridItem>
                </GridContainer>
              </form>
            </CardBody>
          </Card>
        </GridItem>
        {this.renderRedirect()}
      </GridContainer>
    );
  }
}

export default withStyles(newDealFormStyle)(NewDealForm);
