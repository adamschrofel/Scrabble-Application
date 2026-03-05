import { Link, useLocation } from "react-router-dom";
import { ui } from "../ui";

const items = [
  { to: "/finder", label: "Word finder" },
  { to: "/check", label: "Word checker" },
  { to: "/board", label: "Board solver" },
];

export default function PrimaryNav() {
  const { pathname } = useLocation();

  return (
    <nav aria-label="Primary" className="mt-4 flex flex-wrap gap-3">
      {items.map((it) => {
        const active = pathname.startsWith(it.to);
        return (
          <Link
            key={it.to}
            to={it.to}
            aria-current={active ? "page" : undefined}
            className={`${ui.back} ${active ? "border-violet-500/70 bg-violet-950/40" : ""}`}
          >
            {it.label}
          </Link>
        );
      })}
    </nav>
  );
}
