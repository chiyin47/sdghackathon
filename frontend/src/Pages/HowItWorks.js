import React from 'react';
import './CssPages/HowItWorks.css';

function HowItWorks() {
  return (
    <div className="hiw-container">
      <h1 className="hiw-title">How It Works</h1>

      <div className="hiw-step">
        <h2>1. Input Your Route</h2>
        <p>Specify your starting point and destination, then select your preferred mode of transportation.</p>
      </div>

      <div className="hiw-step">
        <h2>2. Calculate Eco-Friendly Routes</h2>
        <p>The app evaluates possible routes using traffic, distance, and CO2 emissions. The application analyzes available routes, taking into account traffic conditions, distance, and environmental impact.</p>
      </div>

      <div className="hiw-step">
        <h2>3. See the Best Route</h2>
        <p>The optimized route is displayed on the map, highlighting estimated travel time and potential CO2 savings.</p>
      </div>
    </div>
  );
}

export default HowItWorks;
