import React from "react";

// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";

// material-ui icons
import Assignment from "@material-ui/icons/Assignment";

// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import Table from "components/Table/Table.jsx";
import Card from "components/Card/Card.jsx";
import CardHeader from "components/Card/CardHeader.jsx";
import CardIcon from "components/Card/CardIcon.jsx";
import CardBody from "components/Card/CardBody.jsx";
import { allDeals } from "../../APIUtils";

import { cardTitle } from "assets/jss/material-dashboard-pro-react.jsx";

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

class RegularTables extends React.Component {
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
    this.state.object.map((object) => listItems.push([object.jurisdiction, object.loanType, "$" + object.capitalAmount + "m", object.interestRate + "%", object.maturity, object.assetClass, object.assetRating, object.issuingPartyRiskProfile, object.status]));
    return (
      <GridContainer>
        <GridItem xs={12}>
          <Card>
            <CardHeader>
              <h4 className={classes.cardIconTitle}>Loans</h4>
            </CardHeader>
            <CardBody>
              <Table
                tableHeaderColor="primary"
                tableHead={["Jurisdiction", "Type", "Capital Amount", "Interest Rate", "Maturity", "Asset Class", "Asset Rating", "Issuing Risk Profile", "Status"]}
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

export default withStyles(style)(RegularTables);
