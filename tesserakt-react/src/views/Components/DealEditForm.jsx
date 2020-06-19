import React from "react";
import { editDeal } from "../../APIUtils";
import FormLabel from "@material-ui/core/FormLabel";
import CustomInput from "components/CustomInput/CustomInput.jsx";
import Button from "components/CustomButtons/Button.jsx";
import Snackbars from "components/Snackbar/Snackbar.jsx";
import InputAdornment from '@material-ui/core/InputAdornment';

export default class DealEditForm extends React.Component {

  state = {
    borrowerName: this.props.borrowerName,
    borrowerDescription: this.props.borrowerDescription,
    interestRate: this.props.interestRate,
    capitalAmount: this.props.capitalAmount,
    maturity: this.props.maturity,
    loanType: this.props.loanType,
    jurisdiction: this.props.jurisdiction
  }

  handleSimple = event => {
    this.setState({ [event.target.name]: event.target.value });
  };

  handleDealEdit = (e) => {
    e.preventDefault();

    editDeal({
      dealId: this.props.dealId,
      borrowerName: this.state.borrowerName,
      borrowerDescription: this.state.borrowerDescription,
      jurisdiction: this.state.jurisdiction,
      capitalAmount: this.state.capitalAmount,
      interestRate: this.state.interestRate,
      loanType: this.state.loanType,
      maturity: this.state.maturity,
      success: false,
      something_went_wrong: false
    }).then(response => {
      Object.assign(response, this.state);
      this.setState({ success: true });
    }).catch(error => {
      this.setState({ something_went_wrong: true });
    });
  }

  render = () => {

    return (
      <div>
        <form>
          <FormLabel>
            Borrower Name:
            </FormLabel>
          <CustomInput formControlProps={{ fullWidth: false }}
            inputProps={{
              name: "borrowerName",
              type: "text",
              onChange: this.handleSimple,
              value: this.state.borrowerName,
              style: { "padding-left": "10px" }
            }}
          /><br />
          <FormLabel>
            Borrower Description:
            </FormLabel>
          <CustomInput formControlProps={{ fullWidth: false }}
            inputProps={{
              name: "borrowerDescription",
              type: "text",
              onChange: this.handleSimple,
              value: this.state.borrowerDescription,
              style: { "padding-left": "10px" }
            }}
          /><br />
          <FormLabel>
            Interest Rate:
            </FormLabel>
          <CustomInput formControlProps={{ fullWidth: false }}
            inputProps={{
              name: "interestRate",
              type: "text",
              onChange: this.handleSimple,
              value: this.state.interestRate,
              step: "any",
              min: 0,
              style: { "padding-left": "10px" },
              startAdornment: <InputAdornment position="start">%</InputAdornment>
            }}
          /><br />
          <FormLabel>
            Maturity:
            </FormLabel>
          <CustomInput formControlProps={{ fullWidth: false }}
            inputProps={{
              name: "maturity",
              type: "number",
              onChange: this.handleSimple,
              value: this.state.maturity,
              style: { "padding-left": "10px" },
              startAdornment: <InputAdornment position="start">days</InputAdornment>
            }}
          /><br />
          <FormLabel>
            Capital Amount:
            </FormLabel>
          <CustomInput formControlProps={{ fullWidth: false }}
            inputProps={{
              name: "capitalAmount",
              type: "number",
              onChange: this.handleSimple,
              value: this.state.capitalAmount,
              style: { "padding-left": "10px" },
              startAdornment: <InputAdornment position="start">$</InputAdornment>
            }}
          /><br />
          <FormLabel>
            Jurisdiction:
            </FormLabel>
          <CustomInput formControlProps={{ fullWidth: false }}
            inputProps={{
              name: "jurisdiction",
              type: "text",
              onChange: this.handleSimple,
              value: this.state.jurisdiction,
              style: { "padding-left": "10px" }
            }}
          /><br />
          <FormLabel>
            Loan Type:
            </FormLabel>
          <CustomInput formControlProps={{ fullWidth: false }}
            inputProps={{
              name: "loanType",
              type: "text",
              onChange: this.handleSimple,
              value: this.state.loanType,
              style: { "padding-left": "10px" }
            }}
          /><br />
          <Button type="primary" htmlType="button" onClick={this.handleDealEdit} color="info" size="lg" block>
            Submit Deal Changes
            </Button>
        </form>


        <div>
          <br />
          <Snackbars place="tl" color="danger" message={'Sorry! Something went wrong. Please try again!'} close open={this.state.something_went_wrong} closeNotification={() => this.setState({ something_went_wrong: false })}
          />
        </div>
        <div>
          <br />
          <Snackbars place="tl" color="success" message={'Deal Successfully Edited!'} close open={this.state.success} closeNotification={() => this.setState({ success: false })} />
        </div>


      </div>
    )
  }
}
