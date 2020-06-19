import React from "react";
import Button from "components/CustomButtons/Button.jsx";
import Card from "components/Card/Card.jsx";
import CardBody from "components/Card/CardBody.jsx";
import Table from "components/Table/Table.jsx";
import { FilePond, File } from 'react-filepond';
import SaveAlt from "@material-ui/icons/SaveAlt";
import Delete from "@material-ui/icons/Delete";
import Security from "@material-ui/icons/Security";
import {getDealFiles, downloadFile, deleteDealFile, makeFileSensitive} from "APIUtils";
import { ACCESS_TOKEN, API_BASE_URL } from '../../constants';
var fileDownload = require('js-file-download');

class FileManagement extends React.Component {

    state = {
        files: [],
        dealFiles: []
    }

    getDealFiles = () => {
        getDealFiles(this.props.dealId).then(response => {
            var dealFiles = [];
            response.forEach(function(file) {
            dealFiles.push([file.key.split("/")[1], file.size.toLocaleString() + " kB", file.lastModified, file.owner.name, 
            <div>
                <Button
                round
                simple
                onClick={() => this.handleFileDownload(file.key)}
                color="info">
                <SaveAlt/>
                </Button>
                {file.owner.id === this.props.currentUser ?
                    <Button
                        round
                        simple
                        onClick={() => this.handleFileDelete(file.key)}
                        color="danger">
                        <Delete/>
                    </Button>
                :
                    ""
                }
                {file.owner.id === this.props.currentUser && !file.sensitive ?
                    <Button
                        round
                        simple
                        onClick={() => this.handleMakeFileSensitive(file.key)}
                        color="warning">
                        <Security/>
                    </Button>
                :
                    ""
                }
            </div>]
            )}.bind(this))

            this.setState({dealFiles: dealFiles})
        }).catch(err => {
            // TODO: Handle files fetching error.
        })
    }

    handleInit() {
        console.log('FilePond instance has initialised', this.pond);
        this.getDealFiles();
    }

    handleFileDownload = (fullFileName) => {
        downloadFile(fullFileName).then(response => {
        fileDownload(response.data, fullFileName.split("/")[1]);
        });
    }

    handleFileDelete = (fullFileName) => {
        deleteDealFile(fullFileName).then(response => {
            this.getDealFiles();
        });
    }
    
    handleMakeFileSensitive = (fullFileName) => {
        makeFileSensitive(fullFileName).then(response => {
            this.getDealFiles();
        });
    }

  render() {
    return (
        <div className="App">
            
        <FilePond ref={ref => this.pond = ref}
                  allowMultiple={true} 
                  maxFiles={3} 
                  server={
                    {
                      url: API_BASE_URL + "/fileManager/" + (this.props.dealId ? this.props.dealId : ""),
                      process: {
                        method: 'POST',
                        withCredentials: false,
                        headers: {authorization: "Bearer " + localStorage.getItem(ACCESS_TOKEN)},
                        timeout: 7000,
                        onload: null,
                        onerror: null
                      }
                    }
                  }
                  oninit={() => this.handleInit() }
                  onupdatefiles={(fileItems) => {
                      this.setState({
                          files: fileItems.map(fileItem => fileItem.file)
                      });
                      var fileItem = fileItems[0]; // Latest file gets appended to front of array
                      if (fileItem !== undefined) {
                        var file = fileItem.file;
                        this.setState(previousState => ({
                          dealFiles: [...previousState.dealFiles, [file.name, file.size.toLocaleString() + " kB", new Date(file.lastModified).toLocaleDateString("en-US") + new Date(file.lastModified).toLocaleTimeString("en-US"), "me",
                          <div>
                            <Button
                              round
                              simple
                              onClick={() => this.handleFileDownload(this.props.dealId + "/" + file.name)}
                              color="info"
                              className="like">
                              <SaveAlt/>
                            </Button>
                            <Button
                              round
                              simple
                              onClick={() => this.handleFileDelete(this.props.dealId + "/" + file.name)}
                              color="danger"
                              className="like">
                              <Delete/>
                            </Button>
                            {this.props.dealStatus === "NEW" ?
                                <Button
                                    round
                                    simple
                                    onClick={() => this.handleMakeFileSensitive(this.props.dealId + "/" + file.name)}
                                    color="warning">
                                    <Security/>
                                </Button>
                            :
                                ""
                            }
                          </div>]]
                        }));
                      }
                  }}>
            
            {/* Update current files  */}
            {this.state.files.map(file => (
                <File key={file} src={file} origin="local" />
            ))}
            
        </FilePond>
        <Card>
          <CardBody>
          <Table
            tableHeaderColor="primary"
            tableHead={["Filename", "Size", "Last Modified", "Owner"]}
            tableData={this.state.dealFiles}
            coloredColls={[0]}
            colorsColls={["primary"]}
          />
          </CardBody>
        </Card>
        </div>
    );
  }
}

export default FileManagement;
