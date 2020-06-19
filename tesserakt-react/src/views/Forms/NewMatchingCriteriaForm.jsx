import FormControl from "@material-ui/core/FormControl";
import FormControlLabel from "@material-ui/core/FormControlLabel";
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
import Checkbox from '@material-ui/core/Checkbox';
import Check from '@material-ui/icons/Check';
// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import React from "react";
import { Redirect } from 'react-router-dom';
import Nouislider from "react-nouislider";
import wNumb from 'wnumb';
// API
import { newMatchingCriteria } from "../../APIUtils";

class NewDealForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      checked: ["AAA", "BBB", "CCC", "AA", "BB", "CC", "NotRated"],
      selectedEnabled: "b",
      jurisdiction: "ANY",
      loanType: "ANY",
      assetClass: "ANY",
      capitalAmount: ["10000000", "500000000"],
      interestRate: [1.1, 9.9],
      maturity: [5, 80],
      responseId: ""
    };
    this.handleSubmit = this.handleSubmit.bind(this);
  }
  handleSimple = (name) => event => {
    if (name !== undefined) {
        this.setState({[name]: event});
    } else {
      this.setState({ [event.target.name]: event.target.value });
    }
  };
  renderRedirect = () => {
    if (this.state.redirect) {
      return <Redirect to={'/issuance/matchingCriteria/' + this.state.responseId} />
    }
  }
  handleToggle(value) {
    const { checked } = this.state;
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    this.setState({
      checked: newChecked
    });
  }

  handleSubmit(event) {
    event.preventDefault();
    newMatchingCriteria({
      jurisdiction: this.state.jurisdiction,
      capitalAmount: this.state.capitalAmount.map(amount => parseFloat(amount.replace(/[^\d.-]/g, ""))),
      interestRate: this.state.interestRate,
      loanType: this.state.loanType,
      maturity: this.state.maturity.map(day => Math.trunc(day)),
      assetClass: this.state.assetClass,
      assetRating: this.state.checked
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
                <h5 className={classes.cardTitle}>Add new lender matching criteria to be notified of matching deals</h5>
              </CardText>
            </CardHeader>
            <CardBody>
              <form onSubmit={this.handleSubmit}>
                <GridContainer>
                  <GridItem xs={12} sm={1}>
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
                        onChange={this.handleSimple(undefined)}
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
                        <MenuItem
                        classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                        }}
                          value="ANY"
                        >
                          ANY
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                <GridItem xs={12} sm={1}>
                    <FormLabel className={classes.labelHorizontal}>
                      Capital Amount
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <br />
                    <br />
                    <div className="slider slider-info">
                      <Nouislider
                        start={this.state.capitalAmount}
                        connect={[false, true, false]}
                        step={1000000}
                        range={{ min: 0, max: 1000000000 }}
                        tooltips = {true}
                        pips= {{ mode: 'count', values: 6, density: 4, format: wNumb({prefix: '$', thousand: ','}) }}
                        name= "capitalAmount"
                        onChange= {this.handleSimple("capitalAmount")}
                        format= {wNumb({prefix: '$', thousand: ',', decimals: 0})}
                      />
                    </div>
                  </GridItem>
                </GridContainer>
                <br />
                <br />
                <GridContainer>
                <GridItem xs={12} sm={1}>
                    <FormLabel className={classes.labelHorizontal}>
                      Interest Rate
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <br />
                    <br />
                    <div className="slider slider-info">
                      <Nouislider
                        start={this.state.interestRate}
                        connect={[false, true, false]}
                        step={0.01}
                        range={{ min: 0, max: 15 }}
                        tooltips = {true}
                        pips= {{ mode: 'count', values: 6, density: 4, format: wNumb({prefix: '%'}) }}
                        name= "interestRate"
                        onChange= {this.handleSimple("interestRate")}
                      />
                    </div>
                  </GridItem>
                </GridContainer>
                <br />
                <br />
                <GridContainer>
                  <GridItem xs={12} sm={1}>
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
                        onChange={this.handleSimple(undefined)}
                      inputProps={{
                          name: "loanType",
                          id: "loanType"
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
                        <MenuItem
                        classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                        }}
                          value="ANY"
                        >
                          ANY
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                <GridItem xs={12} sm={1}>
                    <FormLabel className={classes.labelHorizontal}>
                      Maturity
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <br />
                    <br />
                    <div className="slider slider-info">
                      <Nouislider
                        start={this.state.maturity}
                        connect={[false, true, false]}
                        step={1}
                        range={{ min: 0, max: 1825 }}
                        tooltips = {true}
                        pips= {{ mode: 'count', values: 6, density: 4, format: wNumb({postfix: ' days'}) }}
                        name= "maturity"
                        onChange= {this.handleSimple("maturity")}
                      />
                    </div>
                  </GridItem>
                </GridContainer>
                <br />
                <br />
                <GridContainer>
                  <GridItem xs={12} sm={1}>
                    <FormLabel className={classes.labelHorizontal}>
                      Asset Class
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={6} md={5} lg={5}>
                    <FormControl
                      fullWidth
                      className={classes.selectFormControl}
                    >
                      <InputLabel
                        htmlFor="assetClass"
                        className={classes.selectLabel}
                      >
                        Select Class
                      </InputLabel>
                      <Select
                        MenuProps={{
                          className: classes.selectMenu
                      }}
                        classes={{
                          select: classes.select
                        }}
                        value={this.state.assetClass}
                        onChange={this.handleSimple(undefined)}
                        inputProps={{
                            name: "assetClass",
                            id: "assetClass"
                        }}
                      >
                        <MenuItem
                          classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                      }}
                          value="Investment Grade"
                        >
                          Investment Grade
                        </MenuItem>
                        <MenuItem
                            classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                            }}
                          value="NotRated"
                        >
                          Not Rated
                        </MenuItem>
                        <MenuItem
                        classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                        }}
                          value="ANY"
                        >
                          ANY
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={1}>
                    <FormLabel
                      className={
                        classes.labelHorizontal +
                        " " +
                        classes.labelHorizontalRadioCheckbox
                      }
                    >
                      Asset Rating
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <div className={classes.checkboxAndRadio + " " + classes.checkboxAndRadioHorizontal}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            tabIndex={-1}
                            onClick={() => this.handleToggle("AAA")}
                            checked={this.state.checked.indexOf("AAA") !== -1 ? true:false}
                            icon={<Check className={classes.uncheckedIcon} />}
                            checkedIcon={<Check className={classes.checkedIcon} />}
                            classes={{checked: classes.checked}}
                          />
                        }
                        classes={{label: classes.label}}
                        label="AAA"
                      />
                    </div>
                    <div className={classes.checkboxAndRadio + " " + classes.checkboxAndRadioHorizontal}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            tabIndex={-1}
                            onClick={() => this.handleToggle("BBB")}
                            checked={this.state.checked.indexOf("BBB") !== -1 ? true:false}
                            checkedIcon={<Check className={classes.checkedIcon} />}
                            icon={<Check className={classes.uncheckedIcon} />}
                            classes={{checked: classes.checked}}
                          />
                        }
                        classes={{label: classes.label}}
                        label="BBB"
                      />
                    </div>
                    <div className={classes.checkboxAndRadio + " " + classes.checkboxAndRadioHorizontal}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            tabIndex={-1}
                            onClick={() => this.handleToggle("CCC")}
                            checked={this.state.checked.indexOf("CCC") !== -1 ? true:false}
                            checkedIcon={<Check className={classes.checkedIcon} />}
                            icon={<Check className={classes.uncheckedIcon} />}
                            classes={{checked: classes.checked}}
                          />
                        }
                        classes={{label: classes.label}}
                        label="CCC"
                      />
                    </div>
                    <div className={classes.checkboxAndRadio + " " + classes.checkboxAndRadioHorizontal}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            tabIndex={-1}
                            onClick={() => this.handleToggle("AA")}
                            checked={this.state.checked.indexOf("AA") !== -1 ? true:false}
                            checkedIcon={<Check className={classes.checkedIcon} />}
                            icon={<Check className={classes.uncheckedIcon} />}
                            classes={{checked: classes.checked}}
                          />
                        }
                        classes={{label: classes.label}}
                        label="AA"
                      />
                    </div>
                    <div className={classes.checkboxAndRadio + " " + classes.checkboxAndRadioHorizontal}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            tabIndex={-1}
                            onClick={() => this.handleToggle("BB")}
                            checked={this.state.checked.indexOf("BB") !== -1 ? true:false}
                            checkedIcon={<Check className={classes.checkedIcon} />}
                            icon={<Check className={classes.uncheckedIcon} />}
                            classes={{checked: classes.checked}}
                          />
                        }
                        classes={{label: classes.label}}
                        label="BB"
                      />
                    </div>
                    <div className={classes.checkboxAndRadio + " " + classes.checkboxAndRadioHorizontal}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            tabIndex={-1}
                            onClick={() => this.handleToggle("CC")}
                            checked={this.state.checked.indexOf("CC") !== -1 ? true:false}
                            checkedIcon={<Check className={classes.checkedIcon} />}
                            icon={<Check className={classes.uncheckedIcon} />}
                            classes={{checked: classes.checked}}
                          />
                        }
                        classes={{label: classes.label}}
                        label="CC"
                      />
                    </div>
                    <div className={classes.checkboxAndRadio + " " + classes.checkboxAndRadioHorizontal}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            tabIndex={-1}
                            onClick={() => this.handleToggle("NotRated")}
                            checked={this.state.checked.indexOf("NotRated") !== -1 ? true:false}
                            checkedIcon={<Check className={classes.checkedIcon} />}
                            icon={<Check className={classes.uncheckedIcon} />}
                            classes={{checked: classes.checked}}
                          />
                        }
                        classes={{label: classes.label}}
                        label="Not Rated"
                      />
                    </div>
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
