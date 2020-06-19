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
import { allDeals } from "../../APIUtils";

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

class ManageDeals extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      object: []
    };
    this.componentWillMount = this.componentWillMount.bind(this);
  }

  componentWillMount() {
    allDeals().then(response => {
      this.setState({object: response});
    }).catch(error => {
      // TODO: Do something else in case of error
    });
  }

  render() {
    const { classes } = this.props;
    let listItems = [];
    this.state.object.map((object) => listItems.push([object.syndicate.name, object.borrowerName, object.loanType, "$" + object.capitalAmount.toLocaleString(), object.interestRate + "%", object.maturity, "$"+ object.capitalAmount.toLocaleString(), object.status === "New" ? "Pending" : object.status]));
    return (
      <GridContainer>
        <GridItem xs={12}>
          <Card>
            <CardHeader>
              <h4 className={classes.cardIconTitle}>Manage Deals</h4>
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
      </GridContainer>
    );
  }
}

export default withStyles(style)(ManageDeals);
