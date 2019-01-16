pragma solidity ^0.5.2;

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
        string documentHashesJson;
    }

    mapping (string => Deal) private deals;

    function addDeal(
        string memory dealId,
        string memory underwriterId,
        address underwriterAddress,
        string memory borrowerName,
        string memory jurisdiction,
        string memory capitalAmount,
        string memory interestRate,
        string memory loanType,
        uint maturity,
        string memory assetClass,
        string memory assetRating,
        string memory syndicateObject,
        string memory status,
        string memory documentHashesJson) public {

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
            status: status,
            documentHashesJson: documentHashesJson
            });
    }

    function getDealStatus(string memory dealId) view public returns (string memory) {
        Deal memory deal = deals[dealId];

        return deal.status;
    }

    function getDealSummary(string memory dealId) view public returns (
        string memory,
        string memory,
        string memory,
        string memory,
        uint) {

        Deal memory deal = deals[dealId];
        return (deal.borrowerName, deal.jurisdiction, deal.capitalAmount, deal.interestRate, deal.maturity);
    }

    function updateDeal(
        string memory dealId,
        string memory underwriterId,
        address underwriterAddress,
        string memory borrowerName,
        string memory jurisdiction,
        string memory capitalAmount,
        string memory interestRate,
        string memory loanType,
        uint maturity,
        string memory assetClass,
        string memory assetRating,
        string memory syndicateObject,
        string memory status) public {
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

    function updateDocumentHashes(string memory dealId, string memory documentHashesJson) public {
        deals[dealId].documentHashesJson = documentHashesJson;
    }

    function getDocumentHashes(string memory dealId) view public returns (string memory) {
        return deals[dealId].documentHashesJson;
    }
}
