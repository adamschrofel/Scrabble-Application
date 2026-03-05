import { Link } from "react-router-dom";
import { ui } from "../ui";
import PrimaryNav from "./PrimaryNav";

export default function PageShell({
  title,
  subtitle,
  showNav = true,
  children,
  headerRight,
}) {
  return (
    <div className={ui.page}>
      <div className={`${ui.container} relative`}>
        <div className="absolute inset-0 -z-10 rounded-3xl bg-slate-900/30 blur-2xl" />

        <header className="flex items-start justify-between gap-4">
          <div>
            <Link to="/" className="inline-block">
              <h1 className={ui.h1}>{title}</h1>
            </Link>
            {subtitle ? <p className="mt-2 text-slate-300">{subtitle}</p> : null}
            {showNav ? <PrimaryNav /> : null}
          </div>

          {headerRight ? <div className="pt-2">{headerRight}</div> : null}
        </header>

        <main>{children}</main>
      </div>
    </div>
  );
}
