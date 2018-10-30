pragma solidity ^0.4.19;

contract DealLedger {
    struct Member {
        string id;
        string name;
        string email;
        string contribution;
    }

    struct Deal {
        string underwriterId;
        address underwriterAddress;
        string borrowerName;
        string jurisdiction;
        string capitalAmount;
        string interestRate;
        string loanType;
        uint maturity;
        string assetClass;
        string assetRating;
        string syndicateObject;
        string status;
    }

    mapping (string => Deal) private deals;

    function addDeal(
        string dealId,
        string underwriterId,
        address underwriterAddress,
        string borrowerName,
        string jurisdiction,
        string capitalAmount,
        string interestRate,
        string loanType,
        uint maturity,
        string assetClass,
        string assetRating,
        string syndicateObject,
        string status) public {

        deals[dealId] = Deal({
            underwriterId: underwriterId,
            underwriterAddress: underwriterAddress,
            borrowerName: borrowerName,
            jurisdiction: jurisdiction,
            capitalAmount: capitalAmount,
            interestRate: interestRate,
            loanType: loanType,
            maturity: maturity,
            assetClass: assetClass,
            assetRating: assetRating,
            syndicateObject: syndicateObject,
            status: status
            });
    }

    function getDealStatus(string dealId) view public returns (string) {
        Deal memory deal = deals[dealId];

        return deal.status;
    }

    function getDealSummary(string dealId) view public returns (string, string, string, string, uint) {
        Deal memory deal = deals[dealId];

        return (deal.borrowerName, deal.jurisdiction, deal.capitalAmount, deal.interestRate, deal.maturity);
    }

    function updateDeal(
        string dealId,
        string underwriterId,
        address underwriterAddress,
        string borrowerName,
        string jurisdiction,
        string capitalAmount,
        string interestRate,
        string loanType,
        uint maturity,
        string assetClass,
        string assetRating,
        string syndicateObject,
        string status) public {
        Deal memory deal = deals[dealId];

        deal.underwriterId = underwriterId;
        deal.underwriterAddress = underwriterAddress;
        deal.borrowerName = borrowerName;
        deal.jurisdiction = jurisdiction;
        deal.capitalAmount = capitalAmount;
        deal.interestRate = interestRate;
        deal.loanType = loanType;
        deal.maturity = maturity;
        deal.assetClass = assetClass;
        deal.assetRating = assetRating;
        deal.syndicateObject = syndicateObject;
        deal.status = status;

        deals[dealId] = deal;
    }
}
