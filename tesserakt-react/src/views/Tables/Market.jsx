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
import Button from "components/CustomButtons/Button.jsx";
import Dvr from "@material-ui/icons/Dvr";
import { Redirect } from 'react-router-dom';

import React from "react";
import { allDealsByStatus } from "../../APIUtils";

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

class Market extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      object: [],
      redirectId: ""
    };
    this.componentWillMount = this.componentWillMount.bind(this);
  }

  componentWillMount() {
    allDealsByStatus("NEW").then(response => {
      this.setState({object: response});
    }).catch(error => {
      // TODO: Do something else in case of error
    });
  }

  renderRedirect = () => {
    if (this.state.redirect) {
      return <Redirect to={'/market/' + this.state.redirectId} />
    }
  }

  handleRedirectClick(id) {
    this.setState({redirectId: id});
    this.setState({redirect: true});
  }

  render() {
    const { classes } = this.props;
    let listItems = [];
    this.state.object.map((object) => listItems.push([new Date(object.createdAt).toLocaleString('en-US'), object.borrowerName, object.loanType, "$" + object.capitalAmount.toLocaleString(), object.interestRate + "%", object.maturity, object.assetClass, object.assetRating, object.subscription / object.capitalAmount * 100 + "%",
    (
      <div className="actions-right">
        <Button
          justIcon
          round
          simple
          onClick={() => this.handleRedirectClick(object.id)}
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
              <h4 className={classes.cardIconTitle}>New Deals looking for lenders</h4>
            </CardHeader>
            <CardBody>
              <Table
                tableHeaderColor="primary"
                tableHead={["Created At", "Borrower", "Type", "Amount", "Interest Rate", "Maturity", "Investment Class", "Rating", "Subscription"]}
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

export default withStyles(style)(Market);
