import { useState } from "react";

export default function CO2Calculator() {
  const [transportType, setTransportType] = useState("car");
  const [distance, setDistance] = useState(0);
  const [passengers, setPassengers] = useState(1);
  const [co2, setCo2] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await fetch("http://localhost:8080/api/calculate-co2", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ transportType, distance: Number(distance), passengers: Number(passengers) }),
      });
      if (!response.ok) throw new Error(`Server error ${response.status}`);
      const data = await response.json();
      setCo2(data.co2);
    } catch (err) {
      setError(err.message || "Request failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="co2-calculator">
      <h2>CO₂ Calculator</h2>
      <form onSubmit={handleSubmit}>
        <label>Transport Type:</label>
        <select value={transportType} onChange={(e) => setTransportType(e.target.value)}>
          <option value="corolla">Corolla</option>
          <option value="camry">Camry</option>
          <option value="myvi">Myvi</option>
          <option value="honda city">Honda city</option>
          <option value="city">City</option>
          <option value="focus">Focus</option>
          <option value="ranger">Ranger</option>
          <option value="golf">Golf</option>
          <option value="persona">Persona</option>
          <option value="proton x70">Proton X70</option>
          <option value="nissan almera">Nissan Almera</option>
          <option value="nissan x-trail">Nissan X-Trail</option>
          <option value="mazda 3">Mazda 3</option>
          <option value="mazda cx-5">Mazda CX-5</option>
          <option value="bmw 3-series">BMW 3-Series</option>
          <option value="mercedes c-class">Mercedes C-Class</option>
          <option value="picanto">Picanto</option>
          <option value="sportage">Sportage</option>
        </select>

        <label>Distance (km):</label>
        <input type="number" value={distance} onChange={(e) => setDistance(e.target.value)} />

        <label>Passengers:</label>
        <input type="number" min="1" value={passengers} onChange={(e) => setPassengers(e.target.value)} />

        <button type="submit" disabled={loading}>{loading ? "Calculating..." : "Calculate"}</button>
      </form>

      {error && <p className="error">Error: {error}</p>}

      {co2 !== null && !error && (
        <p>Estimated CO₂: {Number(co2).toFixed(2)} kg</p>
      )}
    </div>
  );
}