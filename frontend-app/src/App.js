import React, { Component } from 'react';
import './App.css';
import { Collapse, Button, CardBody, Card, Container, Row, Col, CardTitle, CardText, Jumbotron } from 'reactstrap';

import jQuery from 'jquery';

class App extends Component {

  constructor(props) {
    super(props);
    this.toggle = this.toggle.bind(this);

    let collapseTemp = []
    for (let i = 0; i < 2; i++) {
      collapseTemp[i] = false
    }
    this.state = { collapse: collapseTemp, posts: [], data: [], isPostsLoading: true, isDataLoading: true };
    this.getRecommendationData = this.getRecommendationData.bind(this);
  }

   getRecommendationData(e) {
    e.preventDefault();
    if (this.state.isDataLoading) {
      jQuery.ajax({
        url: "http://localhost:8080/recommendation/inputRecommendations",
        dataType: 'json',
        type: "GET",
        success: function (data) {
          this.setState({data: data, isDataLoading: false});
        }.bind(this),
        error: function (xhr, status, err) {
            console.log("Error");
        }
    });
      
    }
    if(this.state.isPostsLoading) {
      jQuery.ajax({
        url: "http://localhost:8080/recommendation/inputs",
        dataType: 'json',
        type: "GET",
        success: function (data) {
            this.setState({posts: data, isPostsLoading: false});
        }.bind(this),
        error: function (xhr, status, err) {
            console.log("Error");
        }
    });
    }
  }

  toggle(postNumber) {
    let collapseTemp = this.state.collapse
    collapseTemp[postNumber] = !collapseTemp[postNumber]
    this.setState({collapse: collapseTemp})
  }

  render() {
    return (
      <div>
        <Container>
          <Row justify="center">
              <Col md={{size: 2, offset: 4}}>
              <Button onClick={this.getRecommendationData}>Load Recommendations</Button>
              </Col>
          </Row>
          {
            !this.state.isDataLoading &&
            this.state.data.map((recRes, index) => {
              return (
                      <Row>
                      <Card>
                        <CardBody>
                          <CardTitle>Post {index+1}</CardTitle>
                          <CardText>{this.state.posts[index]}</CardText>
                          <Button onClick={() => this.toggle(index)}>Click to view recommendations</Button>
                        </CardBody>
                      </Card>
                      <Collapse isOpen={this.state.collapse[index]}>
                      {
                        recRes.map((rec, i) => {
                          return (
                            <Card>
                              <CardBody>
                              {rec}
                              </CardBody>
                            </Card>
                          )
                        })
                      }
                      </Collapse>
                      </Row>
              )
            })
          }
          <Row>
            <Jumbotron>
              <h4>Explanation</h4>
              <li>
                Web contents are being scraped using java library called jsoup.
              </li>
              <li>
                The scraped contents are indexed using Lucene based indexing api.
              </li>
              <li>
                Stop words removal and stemming are done using Lucene.
              </li>
              <li>
                For each query (Post), search api from Searcher object is being used to search in indexed documents.
              </li>
              <li>
                The top 10 ranked recommendations are put in results for each post.
              </li>
              <li>
                Indexing and recommendation are being done inside a backend restful web service (Spring Boot).
              </li>
              <li>
                The react application in the front end hits the api and fetches the results and displays it on UI.
              </li>
            </Jumbotron>
          </Row>
        </Container>
      </div>
    );
  }
}

export default App;
