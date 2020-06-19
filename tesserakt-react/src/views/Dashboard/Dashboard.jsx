// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import AccessTime from "@material-ui/icons/AccessTime";
import dashboardStyle from "assets/jss/material-dashboard-pro-react/views/dashboardStyle";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
import CardFooter from "components/Card/CardFooter.jsx";
import CardHeader from "components/Card/CardHeader.jsx";
// core components
import GridContainer from "components/Grid/GridContainer.jsx";
import GridItem from "components/Grid/GridItem.jsx";
import Table from "components/Table/Table.jsx";
import PropTypes from "prop-types";
import React from "react";
// react plugin for creating charts
import ChartistGraph from "react-chartist";
import { dailySalesChart, emailsSubscriptionChart } from "variables/charts";
import { allDealsByStatus } from "APIUtils";

class Dashboard extends React.Component {
  state = {
    value: 0,
    deals: []
  };
  handleChange = (event, value) => {
    this.setState({ value });
  };
  handleChangeIndex = index => {
    this.setState({ value: index });
  };
  componentWillMount = () => {
    allDealsByStatus("IN_PROGRESS").then((response) => {
      this.setState({deals: response});
    }).catch((err) => {
      // TODO: Do something in case of error.
    })
  }
  render() {
    const { classes } = this.props;
    let listItems = [];
    this.state.deals.map((deal) => listItems.push([deal.syndicate.name, deal.borrowerName, deal.loanType, deal.interestRate + "%", deal.maturity, "$" + deal.capitalAmount.toLocaleString(), deal.status.replace("_", " ")]));

    return (
      <div>
        <GridContainer>
          <GridItem xs={12} sm={12} md={6}>
            <Card chart>
              <CardHeader>
                <ChartistGraph
                  className="ct-chart"
                  data={dailySalesChart.data}
                  type="Line"
                  options={dailySalesChart.options}
                  listener={dailySalesChart.animation}
                />
              </CardHeader>
              <CardBody>
                <h4 className={classes.cardTitle}>Total Return</h4>
              </CardBody>
              <CardFooter chart>
                <div className={classes.stats}>
                  <AccessTime /> updated 4 minutes ago
                </div>
              </CardFooter>
            </Card>
          </GridItem>
          <GridItem xs={12} sm={12} md={6}>
            <Card chart>
              <CardHeader>
                <ChartistGraph
                  className="ct-chart"
                  data={emailsSubscriptionChart.data}
                  type="Bar"
                  options={emailsSubscriptionChart.options}
                  responsiveOptions={emailsSubscriptionChart.responsiveOptions}
                  listener={emailsSubscriptionChart.animation}
                />
              </CardHeader>
              <CardBody>
                <h4 className={classes.cardTitle}>Payments Expected</h4>
              </CardBody>
              <CardFooter chart>
                <div className={classes.stats}>
                  <AccessTime /> campaign sent 2 days ago
                </div>
              </CardFooter>
            </Card>
          </GridItem>
        </GridContainer>
        <GridContainer>
          <GridItem xs={12} sm={12} md={12}>
            <Card>
              <CardHeader>
                <h4 className={classes.cardTitleWhite}>Loan Portfolio</h4>
              </CardHeader>
              <CardBody>
                <Table
                  tableHeaderColor="success"
                  tableHead={["Syndicate", "Borrower", "Type", "Interest Rate", "Maturity", "Outstanding", "Status"]}
                  tableData={listItems}
                />
              </CardBody>
            </Card>
          </GridItem>
        </GridContainer>
      </div>
    );
  }
}

Dashboard.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(dashboardStyle)(Dashboard);
