import React from 'react';
import Chatbot from './Chatbot';
import './CssPages/HomePage.css';
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";

function HomePage() {
  return (
    <div className="home-container">

      {/* LEFT TEXT SECTION */}
      <div className="text-section">
        <h1 className="title">
          Reach your<br />
          destination<br />
          <span className="highlight">faster, cheaper,</span><br />
          and greener
        </h1>

        <p className="subtitle">
          Let AI outsmart the traffic. EcoRoute analyses real-time data to 
          automatically reroute you around jams, cutting your commute time 
          and carbon footprint.
        </p>
        <Link to="/demo">
        <button className="get-started-btn">
          Get Started 
         </button>
         </Link>
      </div>

      {/* RIGHT HERO IMAGE */}
      <div className="image-section">
        <img
          src="/Hero.png"
          alt="EcoRoute Hero"
          className="hero-image"
        />
      </div>

      {/* OPTIONAL: Keep Chatbot below page */}
    </div>
  );
}

export default HomePage;
