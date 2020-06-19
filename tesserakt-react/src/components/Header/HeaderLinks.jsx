import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import Grow from "@material-ui/core/Grow";
import Hidden from "@material-ui/core/Hidden";
import MenuItem from "@material-ui/core/MenuItem";
import MenuList from "@material-ui/core/MenuList";
import Paper from "@material-ui/core/Paper";
import Popper from "@material-ui/core/Popper";
// import { Manager, Target, Popper } from "react-popper";
// @material-ui/core components
import withStyles from "@material-ui/core/styles/withStyles";
import Notifications from "@material-ui/icons/Notifications";
import headerLinksStyle from "assets/jss/material-dashboard-pro-react/components/headerLinksStyle";
import classNames from "classnames";
import Button from "components/CustomButtons/Button.jsx";
import PropTypes from "prop-types";
import React from "react";
import { getCurrentUserHasNotification, getCurrentUserNotifications } from "APIUtils";
import { Redirect } from 'react-router-dom';

class HeaderLinks extends React.Component {
  state = {
    open: false,
    newNotification: false,
    notifications: [],
    redirectUrl: "",
    redirect: false
  };
  handleClick = () => {
    getCurrentUserNotifications().then(response => {
      this.setState({ notifications: response });
    }).catch(() => {
      // TODO: What to do here in case of error?
    });
    this.setState({ open: !this.state.open });
  };
  handleClose = () => {
    this.setState({ open: false });
  };
  getUserNewNotification() {
    getCurrentUserHasNotification().then(response => {
      this.setState({newNotification: response});
    }).catch(() => {
      // TODO: What to do here in case of error?
    });
  }
  componentDidMount() {
    this.getUserNewNotification();
    this.interval = setInterval(() => this.getUserNewNotification(), 20000);
  }

  notificationRedirect = (path) => {
    this.setState({redirectUrl: path});
    this.setState({redirect: true});
  }

  renderRedirect = () => {
    if (this.state.redirect) {
      this.setState({redirect: false});
      return <Redirect to={this.state.redirectUrl} />
    }
  }

  render() {
    const { classes, rtlActive } = this.props;
    const { open } = this.state;
    const dropdownItem = classNames(
      classes.dropdownItem,
      classes.primaryHover,
      { [classes.dropdownItemRTL]: rtlActive }
    );
    const wrapper = classNames({
      [classes.wrapperRTL]: rtlActive
    });
    const managerClasses = classNames({
      [classes.managerClasses]: true
    });

    let notificationItems = [];
    this.state.notifications.map((notification) => notificationItems.push(<MenuItem
      onClick={() => this.notificationRedirect(notification.url)}
      className={dropdownItem}
    >
      {notification.message}
    </MenuItem>))

    return (
      <div className={wrapper}>
        <div className={managerClasses}>
          <Button
            color="transparent"
            justIcon
            aria-label="Notifications"
            aria-owns={open ? "menu-list" : null}
            aria-haspopup="true"
            onClick={this.handleClick}
            className={rtlActive ? classes.buttonLinkRTL : classes.buttonLink}
            muiClasses={{
              label: rtlActive ? classes.labelRTL : ""
            }}
            buttonRef={node => {
              this.anchorEl = node;
            }}
          >
            <Notifications
              className={
                classes.headerLinksSvg +
                " " +
                (rtlActive
                  ? classes.links + " " + classes.linksRTL
                  : classes.links)
              }
            />
            { 
              this.state.newNotification ?
                <span className={classes.notifications}>*</span>
              : ""
            }
            <Hidden mdUp implementation="css">
              <span onClick={this.handleClick} className={classes.linkText}>
                {rtlActive ? "إعلام" : "Notification"}
              </span>
            </Hidden>
          </Button>
          <Popper
            open={open}
            anchorEl={this.anchorEl}
            transition
            disablePortal
            placement="bottom"
            className={classNames({
              [classes.popperClose]: !open,
              [classes.pooperResponsive]: true,
              [classes.pooperNav]: true
            })}
          >
            {({ TransitionProps, placement }) => (
              <Grow
                {...TransitionProps}
                id="menu-list"
                style={{ transformOrigin: "0 0 0" }}
              >
                <Paper className={classes.dropdown}>
                  <ClickAwayListener onClickAway={this.handleClose}>
                    <MenuList role="menu">
                      {notificationItems}
                    </MenuList>
                  </ClickAwayListener>
                </Paper>
              </Grow>
            )}
          </Popper>
        </div>
      {this.renderRedirect()}
      </div>
    );
  }
}

HeaderLinks.propTypes = {
  classes: PropTypes.object.isRequired,
  rtlActive: PropTypes.bool
};

export default withStyles(headerLinksStyle)(HeaderLinks);
