import { Link } from "react-router-dom";

export default function PageHeader({ title, right }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 16 }}>
      <Link to="/" className="btn">
        Home
      </Link>

      {title ? <h2 style={{ margin: 0 }}>{title}</h2> : null}

      <div style={{ marginLeft: "auto" }}>{right ?? null}</div>
    </div>
  );
}