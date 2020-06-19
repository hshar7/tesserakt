import React from "react";

class Message extends React.Component {
    render() {
        return (
            <div > {this.props.username}: {this.props.text} </div>
        );
    }
};

class Chat extends React.Component {

    // return the initial state of our Chat class
    state = {
        messages: [],
        users: [],
        chatInput: ''
    }

    // update the input field when the user types something
    setChatInput = (event) => {
        this.setState({ chatInput: event.target.value })
    }

    // send the message to the other users
    sendChat = () => {
        if (this.state.chatInput) {
            this.props.chatRoom.emit('message', {
                text: this.state.chatInput
            });
            this.setState({ chatInput: '' });
        }
    }

    componentDidMount = () => {
        this.props.chatRoom.on('message', (payload) => {
            this.setState({ messages: [...this.state.messages, { key: this.state.messages.length, uuid: payload.sender.uuid, username: payload.sender.state.username, text: payload.data.text }] });
        });
        this.interval = setInterval(this.updateUsers, 5000);

        this.props.chatRoom.search({
            event: 'message'
        }).on('message', (event) => {
            this.setState({ messages: [...this.state.messages, { key: this.state.messages.length, uuid: event.sender.uuid, username: event.sender.state.username, text: event.data.text }] });
        }).on('$.search.finish', () => {
            this.setState({messages: this.state.messages.reverse()});
        });
    }

    updateUsers = () => {
        var newUsers = [];
        for (var userId in this.props.chatRoom.users) { newUsers.push(this.props.chatRoom.users[userId].state.username) };
        this.setState({ users: newUsers });
    }

    // bind the 'Enter' key for sending messages
    _handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            this.sendChat();
        }
    }

    // render the input field and send button
    render = () => {
        var messageList = [];
        this.state.messages.forEach(message => { messageList.push(<Message key={message.key} username={message.username} uuid={message.uuid} text={message.text} />) });
        var userList = [];
        this.state.users.forEach(user => { userList.push(<li>{user}</li>) });

        return (
            <div>
                <div id="chat-output" style={{ "height": "350px", "width": "500px" }} >
                    <div style={{ "overflow": "auto", "max-height": "350px" }} >
                        {messageList}
                    </div>
                </div>
                <div>
                    <input id="chat-input"
                        type="text"
                        name=""
                        value={this.state.chatInput} onChange={this.setChatInput} onKeyPress={this._handleKeyPress}
                    />
                    <input type="button"
                        onClick={this.sendChat} value="Send Chat" />
                </div>
                <br />
                <div>
                    <p>online: </p>
                    <div>
                        <ul>
                            {userList}
                        </ul>
                    </div>
                </div>
            </div >
        );
    }
}

export default Chat;
