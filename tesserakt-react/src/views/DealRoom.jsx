// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import { cardTitle } from "assets/jss/material-dashboard-pro-react.jsx";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
// core components
import CardHeader from "components/Card/CardHeader.jsx";
import Accordion from "components/Accordion/Accordion.jsx";
import FileManagement from "./Components/FileManagement";
import React from "react";
import Snackbars from "components/Snackbar/Snackbar.jsx";
import { getDeal, getCurrentUser, readyUp, subscribeToDeal, deleteDeal, unsubscribeFromDeal } from "../APIUtils";
import { CHAT_PUBLISH_KEY, CHAT_SUBSCRIBE_KEY } from '../constants';
import Button from "components/CustomButtons/Button.jsx";
import CustomInput from "components/CustomInput/CustomInput.jsx";
import FormLabel from "@material-ui/core/FormLabel";
import { Redirect } from 'react-router-dom';
import DealEditForm from "./Components/DealEditForm";
import Chat from "./Components/Chat";
import ChatEngineCore from 'chat-engine';
import ReactDOM from 'react-dom';

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

class DealRoom extends React.Component {
  state = {
    object: {},
    members: [],
    contribution: 0,
    currentUser: "",
    invitee: "",
    success: false,
    something_went_wrong: false,
    redirect: false
  }

  handleSimple = event => {
    this.setState({ [event.target.name]: event.target.value });
  };

  componentWillMount = () => {

    const ChatEngine = ChatEngineCore.create({
      publishKey: CHAT_PUBLISH_KEY,
      subscribeKey: CHAT_SUBSCRIBE_KEY,
      ssl: true
    });

    console.log(ChatEngine);


    ChatEngine.once('$.connected', (payload) => {
      console.log(payload);

      let chat = new ChatEngine.Chat(this.state.object.id, true, {
        name: 'Deal Room ' + this.state.object.id,
        borrower: this.state.object.borrowerName
      });

      ReactDOM.render(
        <Chat chatRoom={chat} />,
        document.getElementById('chat')
      );
    });

    getDeal(this.props.match.params.id).then(response => {
      this.setState({ object: response });
      this.setState({ members: response.syndicate.members })

      // Get current user information
      getCurrentUser().then(userResponse => {
        this.setState({ currentUser: userResponse.id });

        response.syndicate.members.forEach(member => {
          if (member.user.id === userResponse.id) {
            this.setState({ contribution: member.contribution });
          }
        })

        ChatEngine.connect(userResponse.id, userResponse, userResponse.id);

      }).catch(error => {
        this.setState({ something_went_wrong: true });
      });
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

  handleReadyUp = e => {
    e.preventDefault();

    readyUp(this.state.object.id).then(function (response) {
      var member = this.state.members.find(member => member.user.id === response.id);
      member.ready = true;
      var newMembers = this.state.members.filter(member => member.user.id !== response.id);
      newMembers.push(member);
      this.setState({ members: newMembers });
    }.bind(this)).catch(error => {
      this.setState({ something_went_wrong: true });
    })
  }

  handleContributionChange = e => {
    e.preventDefault();

    subscribeToDeal({
      dealId: this.state.object.id,
      userId: this.state.currentUser,
      subscriptionAmount: this.state.contribution
    }).then(function (response) {
      this.setState({ members: response.syndicate.members });
    }.bind(this)).catch(error => {
      this.setState({ something_went_wrong: true });
    });
  }

  handleLeaveDeal = e => {
    e.preventDefault();

    unsubscribeFromDeal(this.state.object.id).then(function (response) {
      this.setState({ redirect: true })
    }.bind(this)).catch(err => {
      this.setState({ something_went_wrong: true });
    })
  }

  renderRedirect = () => {
    if (this.state.redirect) {
      return <Redirect to={'/market/'} />
    }
  }

  render() {
    const { classes } = this.props;
    var memberList = this.state.members.map(member => {
      return <div><h4 className={classes.cardTitle}>{member.user.name} (${member.contribution.toLocaleString()})</h4> status: {member.ready ? <h10 className={classes.success}>Ready</h10> : <h10 className={classes.warning}>Reviewing</h10>} <p>{member.user.organizationName}</p></div>
    })
    return (
      this.state.object && this.state.object.status === 'OPEN' ?
        <GridContainer>
          <GridItem sm={3}>
            <Card>
              <h4 style={{ "padding-left": "10px" }}>Deal</h4>
              {this.state.object.underwriter.id === this.state.currentUser ?
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
                  <Button htmlType="button" onClick={this.handleReadyUp} color="success" size="lg" block>
                    Ready To Close
                </Button>
                </CardBody>
                :
                <CardBody>
                  <h4 className={classes.cardTitle}>Borrower Name</h4>
                  <p>{this.state.object.borrowerName}</p>
                  <h4 className={classes.cardTitle}>Borrower Description</h4>
                  <p>{this.state.object.borrowerDescription}</p>
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
                  <Button htmlType="button" onClick={this.handleReadyUp} color="success" size="lg" block>
                    Ready To Close
                </Button>
                  <Button htmlType="button" onClick={this.handleLeaveDeal} color="danger" size="lg" block>
                    Leave Deal
                </Button>
                </CardBody>
              }
            </Card>
          </GridItem>
          <GridItem sm={6}>
            <Card>
              <h4 style={{ "padding-left": "10px" }}>Discussion/Chat</h4>
              <CardBody>
                <div id="chat"></div>
              </CardBody>
            </Card>
          </GridItem>
          <GridItem sm={3}>
            <Card >
              <h4 style={{ "padding-left": "10px" }}>Participants</h4>
              <CardBody>
                {memberList}
              </CardBody>
            </Card>
            <Card>
              <CardBody>
                <form>
                  <FormLabel className={classes.labelHorizontal}>
                    My Contribution:
              </FormLabel>
                  <CustomInput formControlProps={{ fullWidth: false }}
                    inputProps={{
                      name: "contribution",
                      type: "text",
                      onChange: this.handleSimple,
                      value: this.state.contribution,
                      style: { "padding-left": "10px" }
                    }}
                  /><br />
                  <Button type="primary" htmlType="button" onClick={this.handleContributionChange} color="rose" size="lg" block>
                    Submit Contribution Change
              </Button>
                </form>
              </CardBody>
            </Card>
          </GridItem>
          <GridItem sm={12}>
            <Card>
              <CardHeader>
                <h4 className={classes.cardTitle}>Deal Details</h4>
              </CardHeader>
              <CardBody>
                <Accordion
                  active={0}
                  collapses={[
                    {
                      title: "File Management",
                      content: <FileManagement dealId={this.state.object.id} currentUser={this.state.currentUser} dealStatus={this.state.object.status} />
                    }
                  ]}
                />
              </CardBody>
            </Card>
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
          {this.renderRedirect()}
        </GridContainer>
        :
        <h4>Deal room is not open.</h4>
    );
  }
}

export default withStyles(style)(DealRoom);
