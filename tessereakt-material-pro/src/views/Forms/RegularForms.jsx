import React from "react";

// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import FormLabel from "@material-ui/core/FormLabel";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Datetime from "react-datetime";
import Radio from "@material-ui/core/Radio";
import Checkbox from "@material-ui/core/Checkbox";

// react plugin that creates slider
import Nouislider from "react-nouislider";

// @material-ui/icons
import MailOutline from "@material-ui/icons/MailOutline";
import Check from "@material-ui/icons/Check";
import Clear from "@material-ui/icons/Clear";
import Contacts from "@material-ui/icons/Contacts";
import FiberManualRecord from "@material-ui/icons/FiberManualRecord";

// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import CustomInput from "components/CustomInput/CustomInput.jsx";
import Button from "components/CustomButtons/Button.jsx";
import Card from "components/Card/Card.jsx";
import CardHeader from "components/Card/CardHeader.jsx";
import CardText from "components/Card/CardText.jsx";
import CardIcon from "components/Card/CardIcon.jsx";
import CardBody from "components/Card/CardBody.jsx";
import { Redirect } from 'react-router-dom';
// API
import { newDeal } from "../../APIUtils";

import regularFormsStyle from "assets/jss/material-dashboard-pro-react/views/regularFormsStyle";

class RegularForms extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      checked: [24, 22],
      assetRating: null,
      selectedEnabled: "b",
      jurisdiction: "",
      loanType: "",
      assetClass: "",
      capitalAmount: "",
      interestRate: "",
      maturity: "",
      riskProfile: "",
      responseId: ""
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }
  handleSimple = event => {
    this.setState({ [event.target.name]: event.target.value });
  };
  handleChange(event) {
    this.setState({ assetRating: event.target.value });
  }
  renderRedirect = () => {
    if (this.state.redirect) {
      return <Redirect to={'/issuance/view/' + this.state.responseId} />
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
      assetClass: this.state.assetClass,
      assetRating: this.state.assetRating,
      issuingPartyRiskProfile: this.state.riskProfile
    }).then(response => {
      this.setState({responseId: response['uuid']});
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
                <h4 className={classes.cardTitle}>Deal Details</h4>
              </CardText>
            </CardHeader>
            <CardBody>
              <form onSubmit={this.handleSubmit}>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Governing Provincinal Jurisdiction
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
                        type: "text",
                        onChange: this.handleSimple
                      }}
                      helpText="In $M"
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
                        onChange:this.handleSimple
                      }}
                      helpText="%"
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
                          id: "loanType"
                      }}
                      >
                      <MenuItem
                          classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                      }}
                          value="2"
                        >
                          Term
                        </MenuItem>
                        <MenuItem
                            classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                            }}
                          value="3"
                        >
                          Revolver
                        </MenuItem>
                        <MenuItem
                        classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                        }}
                          value="4"
                        >
                          Any
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
                        onChange:this.handleSimple
                      }}
                      helpText="Days"
                      onChange={this.handleChange}
                    />
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Creation Date
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={12} md={4}>
                    <br />
                    <FormControl fullWidth>
                      <Datetime
                        timeFormat={false}
                        inputProps={{
                          name: "creationDate",
                          id: "creationDate",
                          placeholder: "Pick Date"
                        }}
                      />
                    </FormControl>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
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
                        onChange={this.handleSimple}
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
                          value="2"
                        >
                          Investment Grade
                        </MenuItem>
                        <MenuItem
                            classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                            }}
                          value="3"
                        >
                          Non-rated
                        </MenuItem>
                        <MenuItem
                        classes={{
                            root: classes.selectMenuItem,
                            selected: classes.selectMenuItemSelected
                        }}
                          value="4"
                        >
                          Any
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
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
                    <div
                      className={
                        classes.radio
                      }
                    >
                      <FormControlLabel
                        control={
                          <Radio
                            checked={this.state.assetRating === "AAA"}
                            onChange={this.handleChange}
                            value="AAA"
                            name="assetRating"
                            aria-label="A"
                            icon={
                              <FiberManualRecord
                                className={classes.radioUnchecked}
                              />
                            }
                            checkedIcon={
                              <FiberManualRecord
                                className={classes.radioChecked}
                              />
                            }
                            classes={{
                              checked: classes.radio
                            }}
                          />
                        }
                        classes={{
                          label: classes.label
                        }}
                        label="AAA"
                      />
                    </div>
                    <div
                      className={
                        classes.checkboxAndRadio +
                        " " +
                        classes.checkboxAndRadioHorizontal
                      }
                    >
                      <FormControlLabel
                        control={
                          <Radio
                            checked={this.state.assetRating === "BBB"}
                            onChange={this.handleChange}
                            value="BBB"
                            name="assetRating"
                            aria-label="B"
                            icon={
                              <FiberManualRecord
                                className={classes.radioUnchecked}
                              />
                            }
                            checkedIcon={
                              <FiberManualRecord
                                className={classes.radioChecked}
                              />
                            }
                            classes={{
                              checked: classes.radio
                            }}
                          />
                        }
                        classes={{
                          label: classes.label
                        }}
                        label="BBB"
                      />
                    </div>
                    <div
                      className={
                        classes.checkboxAndRadio +
                        " " +
                        classes.checkboxAndRadioHorizontal
                      }
                    >
                      <FormControlLabel
                        control={
                          <Radio
                            checked={this.state.assetRating === "CCC"}
                            onChange={this.handleChange}
                            value="CCC"
                            name="assetRating"
                            aria-label="B"
                            icon={
                              <FiberManualRecord
                                className={classes.radioUnchecked}
                              />
                            }
                            checkedIcon={
                              <FiberManualRecord
                                className={classes.radioChecked}
                              />
                            }
                            classes={{
                              checked: classes.radio
                            }}
                          />
                        }
                        classes={{
                          label: classes.label
                        }}
                        label="CCC"
                      />
                    </div>
                    <div
                      className={
                        classes.checkboxAndRadio +
                        " " +
                        classes.checkboxAndRadioHorizontal
                      }
                    >
                      <FormControlLabel
                        control={
                          <Radio
                            checked={this.state.assetRating === "Not Rated"}
                            onChange={this.handleChange}
                            value="Not Rated"
                            name="assetRating"
                            aria-label="B"
                            icon={
                              <FiberManualRecord
                                className={classes.radioUnchecked}
                              />
                            }
                            checkedIcon={
                              <FiberManualRecord
                                className={classes.radioChecked}
                              />
                            }
                            classes={{
                              checked: classes.radio
                            }}
                          />
                        }
                        classes={{
                          label: classes.label
                        }}
                        label="Not Rated"
                      />
                    </div>
                    <div
                      className={
                        classes.checkboxAndRadio +
                        " " +
                        classes.checkboxAndRadioHorizontal
                      }
                    >
                      <FormControlLabel
                        control={
                          <Radio
                            checked={this.state.assetRating === "Pending Rating"}
                            onChange={this.handleChange}
                            value="Pending Rating"
                            name="assetRating"
                            aria-label="B"
                            icon={
                              <FiberManualRecord
                                className={classes.radioUnchecked}
                              />
                            }
                            checkedIcon={
                              <FiberManualRecord
                                className={classes.radioChecked}
                              />
                            }
                            classes={{
                              checked: classes.radio
                            }}
                          />
                        }
                        classes={{
                          label: classes.label
                        }}
                        label="Pending Rating"
                      />
                    </div>
                  </GridItem>
                </GridContainer>
                <GridContainer>
                  <GridItem xs={12} sm={2}>
                    <FormLabel className={classes.labelHorizontal}>
                      Issuing Party Risk Profile
                    </FormLabel>
                  </GridItem>
                  <GridItem xs={12} sm={10}>
                    <CustomInput
                      formControlProps={{
                        fullWidth: false
                      }}
                      inputProps={{
                        name: "riskProfile",
                        type: "text",
                        onChange:this.handleSimple
                      }}
                      value={this.handleChange}
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

export default withStyles(regularFormsStyle)(RegularForms);
