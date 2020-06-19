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
import { myMatchingCriteria } from "../../APIUtils";

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
      object: []
    };
    this.componentWillMount = this.componentWillMount.bind(this);
  }

  componentWillMount() {
    myMatchingCriteria().then(response => {
      response = response.map(object => {
        object.capitalAmountMin = object.capitalAmountMin.toLocaleString();
        object.capitalAmountMax = object.capitalAmountMax.toLocaleString();
        return object;
      });
      this.setState({object: response});
    }).catch(error => {
      // TODO: Do something else in case of error
    });
  }

  render() {
    const { classes } = this.props;
    let listItems = [];
    this.state.object.map((object) => listItems.push([object.jurisdiction.join(', '), object.loanType.join(', '), "$" + object.capitalAmountMin + " <> $" + object.capitalAmountMax, object.interestRateMin + "% <> " + object.interestRateMax + "%", object.maturityMin + " <> " + object.maturityMax, object.assetClass.join(', '), object.assetRating.join(', ')]));
    return (
      <GridContainer>
        <GridItem xs={12}>
          <Card>
            <CardHeader>
              <h4 className={classes.cardIconTitle}>My Matching Criteria for upcoming deals</h4>
            </CardHeader>
            <CardBody>
              <Table
                tableHeaderColor="primary"
                tableHead={["Jurisdiction", "Type", "Capital Amount", "Interest Rate", "Maturity", "Asset Class", "Asset Rating"]}
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

export default withStyles(style)(DealsSubscribedTable);
