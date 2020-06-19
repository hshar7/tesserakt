// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import { cardTitle } from "assets/jss/material-dashboard-pro-react.jsx";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import Home from "@material-ui/icons/Home";
import Code from "@material-ui/icons/Code";
import AddShoppingCart from "@material-ui/icons/AddShoppingCart";
import People from "@material-ui/icons/People";
import Attachment from "@material-ui/icons/Attachment";
import Button from "components/CustomButtons/Button.jsx";
import CustomInput from "components/CustomInput/CustomInput.jsx";
// core components
import Tabs from "components/CustomTabs/CustomTabs.jsx";
import chartsStyle from "assets/jss/material-dashboard-pro-react/views/chartsStyle.jsx";
import CardFooter from "components/Card/CardFooter.jsx";
import ChartistGraph from "react-chartist";
import React from "react";
import Snackbars from "components/Snackbar/Snackbar.jsx";
import Table from "components/Table/Table.jsx";
// File stuff
import 'filepond/dist/filepond.min.css';
// API
import { getDeal, subscribeToDeal, deleteDeal, getCurrentUser, inviteToDeal, syndicateRecommendation } from "../APIUtils";
import FileManagement from "./Components/FileManagement";
import DealEditForm from "./Components/DealEditForm";

const style = {
  customCardContentClass: {
    paddingLeft: "0",
    paddingRight: "0"
  },
  cardIconTitle: {
    ...cardTitle,
    marginTop: "15px",
    marginBottom: "0px"
  }
};

class DealRequest extends React.Component {

  state = {
    object: {},
    members: [],
    currentUser: "",
    subscriptionAmount: 0,
    alreadySubscribed: false,
    invitee: "",
    files: [],
    dealFiles: [],
    success: false,
    something_went_wrong: false,
    recommendedUserList: []
  }

  handleSimple = event => {
    this.setState({ [event.target.name]: event.target.value });
  };

  componentWillMount = () => {
    getDeal(this.props.match.params.id).then(response => {
      this.setState({ object: response });
      this.setState({ members: response.syndicate.members })

    }).catch(error => {
      this.setState({ something_went_wrong: true });
    });

    getCurrentUser().then(response => {
      this.setState({ currentUser: response.id });
    }).catch(error => {
      this.setState({ something_went_wrong: true });
    });

    syndicateRecommendation().then(response => {
      var recommendedUserList = [];
      response.forEach(user => { recommendedUserList.push([user.name, user.organizationName, user.email]) })
      this.setState({ recommendedUserList: recommendedUserList })
    })
  }

  handleSubmit = () => {
    subscribeToDeal({
      dealId: this.state.object.id,
      userId: this.state.currentUser,
      subscriptionAmount: this.state.subscriptionAmount
    }).then(response => {
      this.setState({ redirect: true });
    }).catch(error => {
      this.setState({ something_went_wrong: true });
    });
  }

  handleInvite = (e) => {
    e.preventDefault();
    inviteToDeal(this.state.invitee, this.state.object.id).then(response => {
      this.setState({ invitee: "" });
      this.setState({ success: true });
    }).catch(error => {
      this.setState({ something_went_wrong: true });
    });
  }

  handleCancelDeal = e => {
    deleteDeal(this.state.object.id).then(function () {
      this.setState({ redirect: true });
    }.bind(this)).catch(err => {
      this.setState({ something_went_wrong: true });
    })
  }

  render() {
    const { classes } = this.props;
    var memberList = this.state.members.map(member => {
      return <div><h4 className={classes.cardTitle}>{member.user.name}</h4><p>{member.user.organizationName}</p></div>
    })
    return (
      <GridContainer>
        <GridItem xs={12}>
          <Tabs
            title=""
            headerColor="primary"
            tabs={[
              {
                tabName: "Summary",
                tabIcon: Home,
                tabContent: (
                  <Card>
                    {this.state.object.underwriter && this.state.object.underwriter.id === this.state.currentUser ?
                      <CardBody>
                        <DealEditForm
                          dealId={this.state.object.id}
                          borrowerName={this.state.object.borrowerName}
                          borrowerDescription={this.state.object.borrowerDescription}
                          capitalAmount={this.state.object.capitalAmount}
                          interestRate={this.state.object.interestRate}
                          loanType={this.state.object.loanType}
                          maturity={this.state.object.maturity}
                          jurisdiction={this.state.object.jurisdiction}
                        />
                        <h4 className={classes.cardTitle}>Asset Rating</h4>
                        <p>{this.state.object.assetRating}</p>
                        <h4 className={classes.cardTitle}>Asset Class</h4>
                        <p>{this.state.object.assetClass}</p>
                        <Button htmlType="button" onClick={this.handleCancelDeal} color="danger" size="lg" block>
                          Cancel Deal
                        </Button>
                      </CardBody>
                      :
                      <CardBody>
                        <h4 className={classes.cardTitle}>Borrower Name</h4>
                        <p>{this.state.object.borrowerName}</p>
                        <h4 className={classes.cardTitle}>Syndicate</h4>
                        <p>{this.state.object.syndicate ? this.state.object.syndicate.name : ""}</p>
                        <h4 className={classes.cardTitle}>Underwriter</h4>
                        <p>{this.state.object.underwriter ? this.state.object.underwriter.name : ""}</p>
                        <h4 className={classes.cardTitle}>Capital Amount</h4>
                        <p>${this.state.object.capitalAmount ? this.state.object.capitalAmount.toLocaleString() : 0}</p>
                        <h4 className={classes.cardTitle}>Interest Rate</h4>
                        <p>{this.state.object.interestRate}%</p>
                        <h4 className={classes.cardTitle}>Maturity</h4>
                        <p>{this.state.object.maturity} days</p>
                        <h4 className={classes.cardTitle}>Jurisdiction</h4>
                        <p>{this.state.object.jurisdiction}</p>
                        <h4 className={classes.cardTitle}>Loan Type</h4>
                        <p>{this.state.object.loanType}</p>
                        <h4 className={classes.cardTitle}>Rating</h4>
                        <p>{this.state.object.assetRating}</p>
                      </CardBody>
                    }
                  </Card>
                )
              },
              {
                tabName: "Participants",
                tabIcon: People,
                tabContent: (
                  <div>
                    <Card>
                      {memberList}
                    </Card>
                    <Card>
                      <CardBody>
                        <h4 className={classes.cardIconTitle}>Suggested Lenders:</h4>
                        <Table
                          tableHeaderColor="primary"
                          tableHead={["Name", "Organization", "Email"]}
                          tableData={this.state.recommendedUserList}
                          coloredColls={[3]}
                          colorsColls={["primary"]}
                        />
                        <form>
                          <GridContainer>
                            <GridItem xs={12} sm={10}>
                              <GridContainer justify="center">
                                <CustomInput
                                  formControlProps={{
                                    fullWidth: false
                                  }}
                                  inputProps={{
                                    name: "invitee",
                                    type: "text",
                                    onChange: this.handleSimple,
                                    placeholder: "jsmith@gmail.com",
                                    value: this.state.invitee
                                  }}
                                />
                              </GridContainer>
                              <GridContainer justify="center">
                                <GridItem xs={12} sm={12} md={9}>
                                  <Button type="primary" htmlType="button" onClick={this.handleInvite} color="rose" simple size="lg" block>
                                    Invite
                            </Button>
                                </GridItem>
                              </GridContainer>
                            </GridItem>
                          </GridContainer>
                        </form>
                      </CardBody>
                    </Card>
                  </div>
                )
              },
              {
                tabName: "Borrower Details",
                tabIcon: Code,
                tabContent: (
                  <Card>
                    <h4 className={classes.cardTitle}>{this.state.object.borrowerName}</h4>
                    <p>{this.state.object.borrowerDescription}</p>
                  </Card>
                )
              },
              {
                tabName: "Documentation",
                tabIcon: Attachment,
                tabContent: (
                  <FileManagement dealId={this.state.object.id} currentUser={this.state.currentUser} dealStatus={this.state.object.status} />
                )
              },
              {
                tabName: "Subscription",
                tabIcon: AddShoppingCart,
                tabContent: (
                  <div>
                    <GridItem xs={6} sm={6} md={5}>
                      <Card>
                        <CardBody>
                          <ChartistGraph
                            data={
                              {
                                labels: ["$" + (this.state.object.subscription ? this.state.object.subscription.toLocaleString() : 0), (this.state.object.capitalAmount - this.state.object.subscription) ? '$' + (this.state.object.capitalAmount - this.state.object.subscription).toLocaleString() : 0],
                                series: [this.state.object.subscription, this.state.object.capitalAmount - this.state.object.subscription]
                              }
                            }
                            type="Pie"
                            options={
                              {
                                donut: true,
                                donutWidth: 60,
                                startAngle: 270,
                                total: this.state.object.capitalAmount,
                                showLabel: true,
                                height: "230px",
                                donutSolid: false
                              }
                            }
                          />
                        </CardBody>
                        <CardFooter stats className={classes.cardFooter + "center"}>
                          <i className={"fas fa-circle " + classes.info} /> Subscriped{` `}
                          <i className={"fas fa-circle " + classes.danger} /> Unsubscribed{` `}
                        </CardFooter>
                      </Card>
                    </GridItem>
                    {this.state.members.some(member => member.user.id === this.state.currentUser) ?
                      <GridItem xs={6} sm={6} md={5}>
                        <Card>
                          <CardBody>
                          </CardBody>
                          <p>Already subscribed to this deal!</p>
                        </Card>
                      </GridItem>
                      :
                      <GridItem xs={6} sm={6} md={5}>
                        <Card>
                          <CardBody>
                            <form onSubmit={this.handleSubmit}>
                              <GridContainer>
                                <GridItem xs={12} sm={10}>
                                  <GridContainer justify="center">
                                    <CustomInput
                                      formControlProps={{
                                        fullWidth: false
                                      }}
                                      inputProps={{
                                        name: "subscriptionAmount",
                                        type: "text",
                                        onChange: this.handleSimple,
                                        placeholder: "Subscription Amount"
                                      }}
                                    />
                                  </GridContainer>
                                  <GridContainer justify="center">
                                    <GridItem xs={12} sm={12} md={9}>
                                      <Button type="primary" htmlType="submit" color="rose" simple size="lg" block>
                                        Enter Deal
                                </Button>
                                    </GridItem>
                                  </GridContainer>
                                </GridItem>
                              </GridContainer>
                            </form>
                          </CardBody>
                        </Card>
                      </GridItem>
                    }

                  </div>
                )
              }
            ]}
          />
        </GridItem>
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
            message={'Invite sent!'}
            close
            open={this.state.success}
            closeNotification={() => this.setState({ success: false })}
          />
        </div>
      </GridContainer>
    );
  }
}

export default withStyles(chartsStyle, style)(DealRequest);
