import { ui } from "../ui";

export default function ErrorBanner({ message }) {
  if (!message) return null;
  return <div className={ui.error}>{message}</div>;
}
