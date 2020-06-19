// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import { cardTitle } from "assets/jss/material-dashboard-pro-react.jsx";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
import CardHeader from "components/Card/CardHeader.jsx";
// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import Table from "components/Table/Table.jsx";
import React from "react";
import { myOpenDeals } from "../../APIUtils";
import Button from "components/CustomButtons/Button.jsx";
import Dvr from "@material-ui/icons/Dvr";
import { Redirect } from 'react-router-dom';

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

class DealsSubscribedTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      object: [],
      redirectId: null,
      redirectStatus: null
    };
    this.componentWillMount = this.componentWillMount.bind(this);
  }

  componentWillMount() {
    myOpenDeals().then(response => {
      this.setState({ object: response });
    }).catch(error => {
      // TODO: Do something else in case of error
    });
  }

  renderRedirect = () => {
    if (this.state.redirect && this.state.redirectStatus === "OPEN") {
      return <Redirect to={'/issuance/view/' + this.state.redirectId} />
    } else if (this.state.redirect && this.state.redirectStatus === "NEW") {
      return <Redirect to={'/market/' + this.state.redirectId} />
    }
  }

  handleRedirectClick(id, status) {
    this.setState({ redirectId: id });
    this.setState({ redirectStatus: status });
    this.setState({ redirect: true });
  }

  render() {
    const { classes } = this.props;
    let listItems = [];
    this.state.object.map((object) => listItems.push([object.syndicate.name, object.borrowerName, object.loanType, "$" + object.capitalAmount.toLocaleString(), object.interestRate + "%", object.maturity, "$" + object.capitalAmount.toLocaleString(), object.status === "NEW" ? "PENDING" : object.status,
    (
      <div className="actions-right">
        <Button
          justIcon
          round
          simple
          onClick={() => this.handleRedirectClick(object.id, object.status)}
          color="info"
          className="like">
          <Dvr />
        </Button>
      </div>
    )]));
    return (
      <GridContainer>
        <GridItem xs={12}>
          <Card>
            <CardHeader>
              <h4 className={classes.cardIconTitle}>Ongoing Deals I'm Subscribed To</h4>
            </CardHeader>
            <CardBody>
              <Table
                tableHeaderColor="primary"
                tableHead={["Syndicate", "Borrower", "Type", "Capital Amount", "Interest Rate", "Maturity", "Outstanding", "Status"]}
                tableData={listItems}
                coloredColls={[3]}
                colorsColls={["primary"]}
              />
            </CardBody>
          </Card>
        </GridItem>
        {this.renderRedirect()}
      </GridContainer>
    );
  }
}

export default withStyles(style)(DealsSubscribedTable);
