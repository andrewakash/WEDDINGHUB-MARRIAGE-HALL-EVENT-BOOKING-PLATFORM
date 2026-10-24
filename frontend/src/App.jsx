import { useCallback, useEffect, useMemo, useState } from 'react';
import { ArrowDown, ArrowRight, CalendarDays, Check, ChevronRight, CircleUserRound, Heart, LogOut, MapPin, Menu, Search, ShieldCheck, Sparkles, Users, X } from 'lucide-react';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8081/api/v1';
const AUTH_KEY = 'weddinghub.auth';
let csrfToken = '';

function readAuth() {
  try { return JSON.parse(sessionStorage.getItem(AUTH_KEY) || 'null'); } catch { return null; }
}

async function getCsrf() {
  if (csrfToken) return csrfToken;
  const response = await fetch(`${API}/auth/csrf`, { credentials: 'include' });
  if (!response.ok) throw new Error('Could not prepare a secure request. Please reload and try again.');
  const data = await response.json();
  csrfToken = data.token;
  return csrfToken;
}

async function api(path, { method = 'GET', body, basic } = {}) {
  const headers = { Accept: 'application/json' };
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (basic) headers.Authorization = `Basic ${basic}`;
  if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) headers['X-XSRF-TOKEN'] = await getCsrf();
  const response = await fetch(`${API}${path}`, {
    method,
    headers,
    credentials: 'include',
    ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
  });
  const data = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) throw new Error(data?.message || (response.status === 401 ? 'Email or password is incorrect.' : `Request failed (${response.status}).`));
  return data;
}

function encodeBasic(email, password) {
  const bytes = new TextEncoder().encode(`${email.trim()}:${password}`);
  return btoa(String.fromCharCode(...bytes));
}

function money(value) {
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(value || 0);
}

function StatusPill({ children }) {
  return <span className={`status-pill status-${String(children).toLowerCase()}`}>{String(children).replaceAll('_', ' ')}</span>;
}

function VenueCard({ hall, index, onOpen }) {
  return <article className="venue-card">
    <button className={`venue-art venue-art-${index % 4}`} onClick={() => onOpen(hall)} aria-label={`View ${hall.name}`}>
      <span className="venue-art-word">THE WEDDING EDIT</span>
      <span className="venue-monogram">{hall.name?.slice(0, 1) || 'W'}</span>
    </button>
    <button className="venue-card-body" onClick={() => onOpen(hall)}>
      <span className="location"><MapPin size={14} />{hall.city}</span>
      <strong>{hall.name}</strong>
      <span className="address">{hall.address}</span>
      <span className="card-meta"><span><Users size={14} /> Up to {hall.capacity} guests</span><b>{money(hall.pricePerDay)} <small>/ day</small></b></span>
    </button>
  </article>;
}

function AuthDialog({ onClose, onLogin, onRegister, busy, error }) {
  const [tab, setTab] = useState('login');
  const [kind, setKind] = useState('CUSTOMER');
  const [form, setForm] = useState({ fullName: '', email: '', password: '', phone: '', businessName: '', businessDescription: '' });
  const change = (key) => (event) => setForm((current) => ({ ...current, [key]: event.target.value }));
  const submit = (event) => {
    event.preventDefault();
    if (tab === 'login') onLogin(form.email, form.password);
    else if (kind === 'VENDOR') onRegister('vendor', form);
    else onRegister('customer', form);
  };
  return <div className="modal-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose(); }}>
    <section className="auth-dialog" role="dialog" aria-modal="true" aria-labelledby="auth-title">
      <button className="modal-close" onClick={onClose} aria-label="Close"><X size={19} /></button>
      <span className="eyebrow"><span /> WELCOME TO WEDDINGHUB</span>
      <h2 id="auth-title">{tab === 'login' ? 'Your plans, all together.' : 'Let’s get you started.'}</h2>
      <div className="auth-tabs"><button className={tab === 'login' ? 'active' : ''} onClick={() => setTab('login')}>Sign in</button><button className={tab === 'register' ? 'active' : ''} onClick={() => setTab('register')}>Create account</button></div>
      {tab === 'register' && <div className="account-kind"><button className={kind === 'CUSTOMER' ? 'active' : ''} onClick={() => setKind('CUSTOMER')}>I’m planning a wedding</button><button className={kind === 'VENDOR' ? 'active' : ''} onClick={() => setKind('VENDOR')}>I’m a vendor</button></div>}
      <form className="auth-form" onSubmit={submit}>
        {tab === 'register' && <label>Full name<input required maxLength="100" value={form.fullName} onChange={change('fullName')} autoComplete="name" /></label>}
        <label>Email<input required type="email" maxLength="150" value={form.email} onChange={change('email')} autoComplete="email" /></label>
        <label>Password<input required type="password" minLength="12" maxLength="72" value={form.password} onChange={change('password')} autoComplete={tab === 'login' ? 'current-password' : 'new-password'} /><small>{tab === 'register' ? 'Use at least 12 characters.' : ' '}</small></label>
        {tab === 'register' && <label>Phone <span className="optional">Optional</span><input type="tel" maxLength="15" value={form.phone} onChange={change('phone')} autoComplete="tel" /></label>}
        {tab === 'register' && kind === 'VENDOR' && <><label>Business name<input required maxLength="150" value={form.businessName} onChange={change('businessName')} /></label><label>About your business <span className="optional">Optional</span><textarea rows="3" maxLength="10000" value={form.businessDescription} onChange={change('businessDescription')} /></label></>}
        {error && <p className="form-error" role="alert">{error}</p>}
        <button className="primary-button wide" disabled={busy}>{busy ? 'Please wait…' : tab === 'login' ? 'Sign in securely' : 'Create account'} <ArrowRight size={16} /></button>
      </form>
      {tab === 'register' && kind === 'VENDOR' && <p className="approval-note"><ShieldCheck size={14} /> Vendor accounts need admin approval before publishing listings.</p>}
    </section>
  </div>;
}

function VenueDialog({ hall, packages, user, onClose, onBook, busy, error }) {
  const [booking, setBooking] = useState(false);
  const [eventStart, setEventStart] = useState('');
  const [eventEnd, setEventEnd] = useState('');
  const [guestCount, setGuestCount] = useState('');
  const [cateringId, setCateringId] = useState('');
  const [decorationId, setDecorationId] = useState('');
  if (!hall) return null;
  const book = (event) => {
    event.preventDefault();
    onBook({ hallId: hall.id, eventStart, eventEnd, guestCount: Number(guestCount), ...(cateringId ? { cateringPackageId: Number(cateringId) } : {}), ...(decorationId ? { decorationPackageId: Number(decorationId) } : {}) });
  };
  return <div className="modal-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose(); }}>
    <section className="venue-dialog" role="dialog" aria-modal="true" aria-labelledby="venue-title">
      <button className="modal-close" onClick={onClose} aria-label="Close venue details"><X size={19} /></button>
      <div className={`dialog-art venue-art-${hall.id % 4}`}><span>MADE FOR YOUR MOMENT</span></div>
      <div className="venue-dialog-content"><span className="location"><MapPin size={14} />{hall.city}</span><h2 id="venue-title">{hall.name}</h2><p className="address">{hall.address}</p><p className="description">{hall.description || 'A welcoming place for the celebration you have imagined. Gather your favourite people and make the day your own.'}</p>
        <div className="venue-facts"><span><Users size={16} /> Up to {hall.capacity} guests</span><b>{money(hall.pricePerDay)} <small>/ day</small></b></div>
        {user?.role === 'CUSTOMER' ? <>{booking ? <form className="booking-form" onSubmit={book}>
          <h3>Make this your day</h3><label>Event starts<input type="datetime-local" required value={eventStart} onChange={(event) => setEventStart(event.target.value)} /></label><label>Event ends<input type="datetime-local" required value={eventEnd} onChange={(event) => setEventEnd(event.target.value)} /></label><label>Guest count<input type="number" required min="1" max={hall.capacity} value={guestCount} onChange={(event) => setGuestCount(event.target.value)} /></label>
          <label>Catering <span className="optional">Optional</span><select value={cateringId} onChange={(event) => setCateringId(event.target.value)}><option value="">No catering package</option>{packages.catering.map((item) => <option key={item.id} value={item.id}>{item.name} · {money(item.pricePerPerson)} / guest</option>)}</select></label><label>Decoration <span className="optional">Optional</span><select value={decorationId} onChange={(event) => setDecorationId(event.target.value)}><option value="">No decoration package</option>{packages.decorations.map((item) => <option key={item.id} value={item.id}>{item.name} · {money(item.price)}</option>)}</select></label>
          {error && <p className="form-error">{error}</p>}<button className="primary-button wide" disabled={busy}>{busy ? 'Saving your date…' : 'Request booking'} <ArrowRight size={16} /></button>
        </form> : <button className="primary-button wide" onClick={() => setBooking(true)}>Check your date <CalendarDays size={16} /></button>}</> : <p className="sign-in-prompt">Sign in with a customer account to request a booking.</p>}
      </div>
    </section>
  </div>;
}

function BookingCard({ booking, onCancel, onPay, busy }) {
  return <article className="booking-card"><div className="booking-top"><div><span className="location"><MapPin size={13} />{booking.city}</span><h3>{booking.hallName}</h3></div><StatusPill>{booking.status}</StatusPill></div><p><CalendarDays size={14} />{new Date(booking.eventStart).toLocaleString()} – {new Date(booking.eventEnd).toLocaleString()}</p><div className="booking-bottom"><span>{booking.guestCount} guests · {money(booking.totalAmount)}</span><div>{booking.status === 'PENDING' && <><button className="small-button" disabled={busy} onClick={() => onPay(booking)}>Pay demo</button><button className="plain-button" disabled={busy} onClick={() => onCancel(booking)}>Cancel</button></>}</div></div>{booking.payments?.map((payment) => <small className="payment-note" key={payment.id}>Payment {payment.status.toLowerCase()} · {payment.paymentMethod}</small>)}</article>;
}

function Empty({ title, detail }) {
  return <div className="empty-state"><span><Sparkles size={18} /></span><h3>{title}</h3><p>{detail}</p></div>;
}

export default function App() {
  const [auth, setAuth] = useState(readAuth);
  const [authOpen, setAuthOpen] = useState(false);
  const [authError, setAuthError] = useState('');
  const [authBusy, setAuthBusy] = useState(false);
  const [view, setView] = useState('browse');
  const [hallFilters, setHallFilters] = useState({ city: '', minimumCapacity: '', maximumPrice: '' });
  const [halls, setHalls] = useState([]);
  const [loadingHalls, setLoadingHalls] = useState(true);
  const [hallError, setHallError] = useState('');
  const [selectedHall, setSelectedHall] = useState(null);
  const [packages, setPackages] = useState({ catering: [], decorations: [] });
  const [actionError, setActionError] = useState('');
  const [actionBusy, setActionBusy] = useState(false);
  const [dashboard, setDashboard] = useState({});
  const [dashboardBusy, setDashboardBusy] = useState(false);
  const [dashboardError, setDashboardError] = useState('');
  const [notice, setNotice] = useState('');
  const [adminTab, setAdminTab] = useState('vendors');
  const [vendorTab, setVendorTab] = useState('halls');
  const [mobileMenu, setMobileMenu] = useState(false);

  const request = useCallback((path, options = {}) => api(path, { ...options, basic: options.basic ?? auth?.basic }), [auth?.basic]);
  const loadHalls = useCallback(async (event) => {
    event?.preventDefault();
    setLoadingHalls(true);
    setHallError('');
    const params = new URLSearchParams();
    Object.entries(hallFilters).forEach(([key, value]) => { if (String(value).trim()) params.set(key, String(value).trim()); });
    try { setHalls(await request(`/halls/search?${params}`)); }
    catch (error) { setHalls([]); setHallError(error.message); }
    finally { setLoadingHalls(false); }
  }, [hallFilters, request]);

  useEffect(() => { loadHalls(); }, []); // Initial public venue catalogue.
  useEffect(() => {
    let alive = true;
    Promise.allSettled([request('/catering-packages'), request('/decoration-packages')]).then((results) => {
      if (!alive) return;
      setPackages({ catering: results[0].status === 'fulfilled' ? results[0].value : [], decorations: results[1].status === 'fulfilled' ? results[1].value : [] });
    });
    return () => { alive = false; };
  }, [request]);

  const loadDashboard = useCallback(async () => {
    if (!auth) return;
    setDashboardBusy(true);
    setDashboardError('');
    try {
      if (auth.user.role === 'CUSTOMER') {
        const bookings = await request('/bookings');
        setDashboard({ bookings });
      } else if (auth.user.role === 'VENDOR') {
        const [profile, ownHalls, catering, decorations, bookings] = await Promise.all([
          request('/vendor/profile'), request('/vendor/halls'), request('/vendor/catering-packages'), request('/vendor/decoration-packages'), request('/vendor/bookings').catch(() => []),
        ]);
        setDashboard({ profile, halls: ownHalls, catering, decorations, bookings });
      } else {
        const [vendors, users, hallsAll, catering, decorations, bookings] = await Promise.all([
          request('/admin/vendors'), request('/admin/users'), request('/admin/halls'), request('/admin/catering-packages'), request('/admin/decoration-packages'), request('/admin/bookings'),
        ]);
        setDashboard({ vendors, users, halls: hallsAll, catering, decorations, bookings });
      }
    } catch (error) { setDashboardError(error.message); }
    finally { setDashboardBusy(false); }
  }, [auth, request]);

  useEffect(() => { if (view === 'dashboard' && auth) loadDashboard(); }, [view, auth, loadDashboard]);

  const openAuth = () => { setAuthError(''); setAuthOpen(true); };
  const login = async (email, password) => {
    setAuthBusy(true); setAuthError('');
    try {
      const basic = encodeBasic(email, password);
      const user = await api('/auth/me', { basic });
      const next = { basic, user };
      sessionStorage.setItem(AUTH_KEY, JSON.stringify(next));
      setAuth(next); setAuthOpen(false); setView('dashboard'); setNotice(`Welcome back, ${user.fullName}.`);
    } catch (error) { setAuthError(error.message); }
    finally { setAuthBusy(false); }
  };
  const register = async (kind, form) => {
    setAuthBusy(true); setAuthError('');
    try {
      const body = kind === 'vendor'
        ? { fullName: form.fullName, email: form.email, password: form.password, phone: form.phone, businessName: form.businessName, businessDescription: form.businessDescription }
        : { fullName: form.fullName, email: form.email, password: form.password, phone: form.phone };
      await api(kind === 'vendor' ? '/auth/vendors/register' : '/auth/register', { method: 'POST', body });
      await login(form.email, form.password);
      if (kind === 'vendor') setNotice('Vendor account created. Your profile is pending admin approval.');
    } catch (error) { setAuthError(error.message); setAuthBusy(false); }
  };
  const logout = () => { sessionStorage.removeItem(AUTH_KEY); csrfToken = ''; setAuth(null); setDashboard({}); setView('browse'); setNotice('You’re signed out.'); };
  const act = async (operation, success) => {
    setActionBusy(true); setActionError('');
    try { await operation(); if (success) setNotice(success); await loadDashboard(); }
    catch (error) { setActionError(error.message); }
    finally { setActionBusy(false); }
  };
  const createBooking = async (body) => {
    await act(async () => { await request('/bookings', { method: 'POST', body }); setSelectedHall(null); setView('dashboard'); }, 'Booking request saved. You can follow it from your dashboard.');
  };
  const searchForm = <form className="search-panel" onSubmit={loadHalls}>
    <label><span>DESTINATION</span><div><MapPin size={16} /><input placeholder="City or destination" value={hallFilters.city} onChange={(event) => setHallFilters({ ...hallFilters, city: event.target.value })} /></div></label>
    <label><span>GUESTS</span><div><Users size={16} /><select value={hallFilters.minimumCapacity} onChange={(event) => setHallFilters({ ...hallFilters, minimumCapacity: event.target.value })}><option value="">Any guest count</option><option value="100">100+ guests</option><option value="250">250+ guests</option><option value="500">500+ guests</option></select></div></label>
    <label><span>MAX BUDGET / DAY</span><div><span className="rupee">₹</span><input type="number" min="1" placeholder="No limit" value={hallFilters.maximumPrice} onChange={(event) => setHallFilters({ ...hallFilters, maximumPrice: event.target.value })} /></div></label>
    <button className="primary-button search-submit"><Search size={17} /><span>Find venues</span></button>
  </form>;

  return <div className="site-shell">
    <header className="site-header"><a className="wordmark" href="#top" onClick={() => setView('browse')}><span className="brand-mark">w.</span>wedding<span>hub</span></a><button className="mobile-menu" onClick={() => setMobileMenu(!mobileMenu)} aria-label="Toggle menu"><Menu size={19} /></button><nav className={mobileMenu ? 'nav-open' : ''}><button onClick={() => { setView('browse'); setMobileMenu(false); }}>Find a venue</button><a href="#how-it-works" onClick={() => setMobileMenu(false)}>How it works</a>{auth && <button onClick={() => { setView('dashboard'); setMobileMenu(false); }}>My {auth.user.role === 'CUSTOMER' ? 'wedding' : 'dashboard'}</button>}</nav><div className="header-actions">{auth ? <><span className="user-greeting"><CircleUserRound size={16} />{auth.user.fullName.split(' ')[0]}</span><button className="header-link" onClick={logout}><LogOut size={14} /> Sign out</button></> : <><button className="header-link" onClick={openAuth}>Sign in</button><button className="header-cta" onClick={openAuth}>Get started <ArrowRight size={14} /></button></>}</div></header>

    {view === 'browse' ? <main id="top">
      <section className="hero"><div className="hero-copy"><span className="eyebrow"><span /> YOUR DAY, YOUR WAY</span><h1>Somewhere<br />to say <i>“I do.”</i></h1><p>Find the place that feels like your story. Thoughtful wedding venues, all in one lovely little corner.</p><a className="text-link" href="#venues">Find your kind of place <ArrowRight size={16} /></a><div className="hero-trust"><span>★★★★★</span><p><b>One place to begin</b><br />Venues, details, and your next chapter.</p></div></div><div className="hero-art"><div className="art-window"><i /><i /><i /><i /></div><div className="art-vase"><i /><i /><i /></div><div className="art-caption"><span>THE WEDDING EDIT</span><b>A day that feels like you.</b></div><span className="art-index">01 / 03</span></div>{searchForm}</section>
      <section className="venues-section" id="venues"><div className="section-heading"><div><span className="eyebrow"><span /> A PLACE TO CELEBRATE</span><h2>Find your <i>somewhere.</i></h2><p>Spaces with a little magic, for the moments that mean everything.</p></div><span className="collection-note"><Sparkles size={15} /> A collection for your celebration</span></div><div className="results-line"><span>{loadingHalls ? 'Finding your venues…' : `${halls.length} ${halls.length === 1 ? 'venue' : 'venues'} to explore`}</span><span>Thoughtfully chosen, ready for your story</span></div>{loadingHalls ? <Empty title="Finding lovely places" detail="Your next chapter is just around the corner." /> : hallError ? <div className="empty-state"><h3>We’ll find your place</h3><p>{hallError}</p><button className="outline-button" onClick={() => loadHalls()}>Try again</button></div> : halls.length ? <div className="venue-grid">{halls.map((hall, index) => <VenueCard key={hall.id} hall={hall} index={index} onOpen={setSelectedHall} />)}</div> : <Empty title="No venues found just yet" detail="Try another city or adjust your guest count and budget." />}</section>
      <section className="story-section" id="how-it-works"><div className="story-mark">w<span>.</span></div><div><span className="eyebrow"><span /> YOUR STORY, BEAUTIFULLY BEGUN</span><h2>Good things start<br />with <i>somewhere.</i></h2></div><div className="story-copy"><p>Start with a feeling. Find a venue that fits your people, your plans, and the kind of day you want to remember.</p><button className="text-link" onClick={() => document.getElementById('venues')?.scrollIntoView({ behavior: 'smooth' })}>Explore the collection <ArrowRight size={16} /></button></div></section>
    </main> : <main className="dashboard-page" id="top"><section className="dashboard-heading"><div><span className="eyebrow"><span /> {auth?.user.role === 'ADMIN' ? 'WEDDINGHUB CONTROL ROOM' : auth?.user.role === 'VENDOR' ? 'YOUR BUSINESS, BEAUTIFULLY MANAGED' : 'YOUR WEDDING, ALL TOGETHER'}</span><h1>{auth?.user.role === 'ADMIN' ? 'Admin dashboard' : auth?.user.role === 'VENDOR' ? 'Vendor dashboard' : 'Your wedding dashboard'}</h1><p>Welcome back, {auth?.user.fullName}. Here’s where things stand.</p></div><button className="outline-button" onClick={() => setView('browse')}>Explore venues <ArrowRight size={15} /></button></section>
      {dashboardError && <div className="inline-alert">{dashboardError} <button onClick={loadDashboard}>Retry</button></div>}{actionError && <div className="inline-alert">{actionError}<button onClick={() => setActionError('')} aria-label="Dismiss"><X size={14} /></button></div>}{dashboardBusy && <div className="loading-line"><span className="loader" /> Loading your dashboard…</div>}
      {auth?.user.role === 'CUSTOMER' && <section className="dashboard-section"><div className="dashboard-section-title"><div><span className="eyebrow"><span /> YOUR PLANS</span><h2>Booking requests</h2></div><span className="count-badge">{dashboard.bookings?.length || 0}</span></div>{dashboard.bookings?.length ? <div className="booking-list">{dashboard.bookings.map((booking) => <BookingCard key={booking.id} booking={booking} busy={actionBusy} onCancel={(item) => act(() => request(`/bookings/${item.id}/cancel`, { method: 'POST' }), 'Booking cancelled.')} onPay={(item) => act(() => request(`/bookings/${item.id}/payments`, { method: 'POST', body: { paymentMethod: 'DEMO' } }), 'Demo payment created. An admin must reconcile it; no charge was made.')} />)}</div> : !dashboardBusy && <Empty title="Your plans start here" detail="Browse venues and request the date that feels right." />}</section>}
      {auth?.user.role === 'VENDOR' && <VendorDashboard dashboard={dashboard} tab={vendorTab} setTab={setVendorTab} request={request} reload={loadDashboard} busy={actionBusy} act={act} />}
      {auth?.user.role === 'ADMIN' && <AdminDashboard dashboard={dashboard} tab={adminTab} setTab={setAdminTab} request={request} reload={loadDashboard} busy={actionBusy} act={act} />}
    </main>}

    <footer className="site-footer"><a className="wordmark" href="#top" onClick={() => setView('browse')}><span className="brand-mark">w.</span>wedding<span>hub</span></a><span>A little help with your forever.</span><span>© 2026 WeddingHub</span></footer>
    {notice && <div className="toast" role="status"><Check size={16} />{notice}<button onClick={() => setNotice('')} aria-label="Dismiss"><X size={15} /></button></div>}
    {authOpen && <AuthDialog onClose={() => setAuthOpen(false)} onLogin={login} onRegister={register} busy={authBusy} error={authError} />}
    <VenueDialog hall={selectedHall} packages={packages} user={auth?.user} onClose={() => { setSelectedHall(null); setActionError(''); }} onBook={createBooking} busy={actionBusy} error={actionError} />
  </div>;
}

function VendorDashboard({ dashboard, tab, setTab, request, reload, busy, act }) {
  if (!dashboard.profile) return null;
  const profile = dashboard.profile;
  const tabs = ['halls', 'catering', 'decorations', 'bookings'];
  const createHall = (event) => {
    event.preventDefault(); const form = new FormData(event.currentTarget);
    act(() => request('/vendor/halls', { method: 'POST', body: { name: form.get('name'), city: form.get('city'), address: form.get('address'), description: form.get('description'), capacity: Number(form.get('capacity')), pricePerDay: Number(form.get('pricePerDay')) } }), 'Venue saved as inactive. Activate it when it’s ready to publish.'); event.currentTarget.reset();
  };
  const createCatering = (event) => {
    event.preventDefault(); const form = new FormData(event.currentTarget);
    act(() => request('/vendor/catering-packages', { method: 'POST', body: { name: form.get('name'), description: form.get('description'), foodType: form.get('foodType'), pricePerPerson: Number(form.get('price')) } }), 'Catering package created.'); event.currentTarget.reset();
  };
  const createDecoration = (event) => {
    event.preventDefault(); const form = new FormData(event.currentTarget);
    act(() => request('/vendor/decoration-packages', { method: 'POST', body: { name: form.get('name'), description: form.get('description'), price: Number(form.get('price')) } }), 'Decoration package created.'); event.currentTarget.reset();
  };
  return <section className="dashboard-section vendor-dash"><div className="vendor-status"><div><span className="eyebrow"><span /> BUSINESS PROFILE</span><h2>{profile.businessName}</h2><p>{profile.businessDescription || 'Add a description to tell couples about your business.'}</p></div><StatusPill>{profile.approvalStatus}</StatusPill></div>{profile.approvalStatus !== 'APPROVED' ? <Empty title={profile.approvalStatus === 'REJECTED' ? 'Your profile needs an update' : 'Your profile is being reviewed'} detail={profile.approvalStatus === 'REJECTED' ? 'Contact WeddingHub support for next steps.' : 'Once approved, you can publish venues and packages and manage customer requests.'} /> : <><div className="dashboard-tabs">{tabs.map((item) => <button key={item} className={tab === item ? 'active' : ''} onClick={() => setTab(item)}>{item[0].toUpperCase() + item.slice(1)}</button>)}</div>{tab === 'halls' && <><div className="management-grid">{dashboard.halls?.map((hall) => <article className="manage-card" key={hall.id}><div><StatusPill>{hall.status}</StatusPill><h3>{hall.name}</h3><p>{hall.city} · {hall.capacity} guests</p><b>{money(hall.pricePerDay)} / day</b></div><button className="outline-button" disabled={busy} onClick={() => act(() => request(`/vendor/halls/${hall.id}/status`, { method: 'PATCH', body: { status: hall.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' } }), hall.status === 'ACTIVE' ? 'Venue unpublished.' : 'Venue published.')}>{hall.status === 'ACTIVE' ? 'Unpublish' : 'Publish'}</button></article>)}</div><form className="create-form" onSubmit={createHall}><h3>Add a venue</h3><div className="form-grid"><label>Venue name<input required name="name" maxLength="150" /></label><label>City<input required name="city" maxLength="100" /></label><label>Address<input required name="address" maxLength="255" /></label><label>Guest capacity<input required type="number" min="1" name="capacity" /></label><label>Price per day (₹)<input required type="number" min="1" step="0.01" name="pricePerDay" /></label><label className="span-two">Description<textarea name="description" rows="2" /></label></div><button className="primary-button" disabled={busy}>Save venue <ArrowRight size={15} /></button></form></>}{tab === 'catering' && <><div className="management-grid">{dashboard.catering?.map((item) => <PackageCard key={item.id} item={item} price={`${money(item.pricePerPerson)} / guest`} onToggle={() => act(() => request(`/vendor/catering-packages/${item.id}/status`, { method: 'PATCH', body: { status: item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' } }), 'Catering status updated.')} busy={busy} />)}</div><form className="create-form compact-form" onSubmit={createCatering}><h3>Add catering</h3><div className="form-grid"><label>Name<input required name="name" maxLength="150" /></label><label>Food type<select name="foodType"><option>VEG</option><option>NON_VEG</option><option>BOTH</option></select></label><label>Price per person (₹)<input required type="number" name="price" min="1" step="0.01" /></label><label>Description<input name="description" /></label></div><button className="primary-button" disabled={busy}>Add package <ArrowRight size={15} /></button></form></>}{tab === 'decorations' && <><div className="management-grid">{dashboard.decorations?.map((item) => <PackageCard key={item.id} item={item} price={money(item.price)} onToggle={() => act(() => request(`/vendor/decoration-packages/${item.id}/status`, { method: 'PATCH', body: { status: item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' } }), 'Decoration status updated.')} busy={busy} />)}</div><form className="create-form compact-form" onSubmit={createDecoration}><h3>Add decoration</h3><div className="form-grid"><label>Name<input required name="name" maxLength="150" /></label><label>Price (₹)<input required type="number" name="price" min="1" step="0.01" /></label><label className="span-two">Description<input name="description" /></label></div><button className="primary-button" disabled={busy}>Add package <ArrowRight size={15} /></button></form></>}{tab === 'bookings' && <BookingManagement bookings={dashboard.bookings || []} onUpdate={(item, status) => act(() => request(`/vendor/bookings/${item.id}/status`, { method: 'PATCH', body: { status } }), `Booking marked ${status.toLowerCase()}.`)} busy={busy} />}</>}</section>;
}

function PackageCard({ item, price, onToggle, busy }) {
  return <article className="manage-card"><div><StatusPill>{item.status}</StatusPill><h3>{item.name}</h3><p>{item.description || 'No description added.'}</p><b>{price}</b></div><button className="outline-button" disabled={busy} onClick={onToggle}>{item.status === 'ACTIVE' ? 'Unpublish' : 'Publish'}</button></article>;
}

function BookingManagement({ bookings, onUpdate, busy, admin = false }) {
  return bookings.length ? <div className="table-wrap"><table><thead><tr><th>Booking</th><th>Customer</th><th>Event</th><th>Amount</th><th>Status</th><th>Action</th></tr></thead><tbody>{bookings.map((booking) => <tr key={booking.id}><td>#{booking.id}<br /><small>{booking.hallName}</small></td><td>{booking.customerName}</td><td>{new Date(booking.eventStart).toLocaleString()}<br /><small>{booking.guestCount} guests</small></td><td>{money(booking.totalAmount)}</td><td><StatusPill>{booking.status}</StatusPill></td><td>{booking.status === 'PENDING' && <><button className="table-action" disabled={busy} onClick={() => onUpdate(booking, 'CONFIRMED')}>Confirm</button><button className="table-action danger" disabled={busy} onClick={() => onUpdate(booking, 'CANCELLED')}>Cancel</button></>}{booking.status === 'CONFIRMED' && <button className="table-action" disabled={busy} onClick={() => onUpdate(booking, 'COMPLETED')}>Complete</button>}</td></tr>)}</tbody></table></div> : <Empty title="No booking requests yet" detail="New customer requests will appear here." />;
}

function AdminDashboard({ dashboard, tab, setTab, request, busy, act }) {
  const tabs = ['vendors', 'venues', 'packages', 'bookings', 'payments', 'users'];
  const updateVendor = (vendor, status) => act(() => request(`/admin/vendors/${vendor.id}/approval`, { method: 'PATCH', body: { status } }), `Vendor ${status.toLowerCase()}.`);
  const updateStatus = (path, item, status, label) => act(() => request(`${path}/${item.id}/status`, { method: 'PATCH', body: { status } }), `${label} ${status.toLowerCase()}.`);
  return <section className="dashboard-section"><div className="dashboard-tabs admin-tabs">{tabs.map((item) => <button key={item} className={tab === item ? 'active' : ''} onClick={() => setTab(item)}>{item[0].toUpperCase() + item.slice(1)}</button>)}</div>
    {tab === 'vendors' && (dashboard.vendors?.length ? <div className="management-grid">{dashboard.vendors.map((vendor) => <article className="manage-card" key={vendor.id}><div><StatusPill>{vendor.approvalStatus}</StatusPill><h3>{vendor.businessName}</h3><p>{vendor.businessDescription || 'No business description.'}</p><small>Account #{vendor.userId}</small></div><div className="manage-actions">{vendor.approvalStatus !== 'APPROVED' && <button className="primary-button" disabled={busy} onClick={() => updateVendor(vendor, 'APPROVED')}>Approve <Check size={14} /></button>}{vendor.approvalStatus !== 'REJECTED' && <button className="outline-button" disabled={busy} onClick={() => updateVendor(vendor, 'REJECTED')}>Reject</button>}</div></article>)}</div> : <Empty title="No vendor profiles yet" detail="New vendor applications will appear here." />)}
    {tab === 'venues' && (dashboard.halls?.length ? <div className="table-wrap"><table><thead><tr><th>Venue</th><th>City</th><th>Vendor</th><th>Capacity</th><th>Price/day</th><th>Status</th><th>Action</th></tr></thead><tbody>{dashboard.halls.map((hall) => <tr key={hall.id}><td>{hall.name}</td><td>{hall.city}</td><td>#{hall.vendorId}</td><td>{hall.capacity}</td><td>{money(hall.pricePerDay)}</td><td><StatusPill>{hall.status}</StatusPill></td><td><button className="table-action" disabled={busy} onClick={() => updateStatus('/admin/halls', hall, hall.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE', 'Venue')}>{hall.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}</button></td></tr>)}</tbody></table></div> : <Empty title="No venues yet" detail="Vendor listings will appear here." />)}
    {tab === 'packages' && <div className="admin-package-lists"><PackageAdminList title="Catering" path="/admin/catering-packages" items={dashboard.catering || []} update={updateStatus} busy={busy} /><PackageAdminList title="Decoration" path="/admin/decoration-packages" items={dashboard.decorations || []} update={updateStatus} busy={busy} /></div>}
    {tab === 'bookings' && <BookingManagement admin bookings={dashboard.bookings || []} onUpdate={(booking, status) => act(() => request(`/admin/bookings/${booking.id}/status`, { method: 'PATCH', body: { status } }), `Booking marked ${status.toLowerCase()}.`)} busy={busy} />}
    {tab === 'payments' && <PaymentAdmin bookings={dashboard.bookings || []} request={request} act={act} busy={busy} />}
    {tab === 'users' && (dashboard.users?.length ? <div className="table-wrap"><table><thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Joined</th></tr></thead><tbody>{dashboard.users.map((user) => <tr key={user.id}><td>{user.fullName}</td><td>{user.email}</td><td>{user.phone || '—'}</td><td><StatusPill>{user.role}</StatusPill></td><td>{user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '—'}</td></tr>)}</tbody></table></div> : <Empty title="No accounts found" detail="Registered customer and vendor accounts will appear here." />)}
  </section>;
}

function PackageAdminList({ title, path, items, update, busy }) {
  return <section className="admin-package-group"><h3>{title} packages</h3>{items.length ? items.map((item) => <div className="package-row" key={item.id}><div><b>{item.name}</b><small>Vendor #{item.vendorId} · {money(item.pricePerPerson ?? item.price)}{item.pricePerPerson ? ' / guest' : ''}</small></div><StatusPill>{item.status}</StatusPill><button className="table-action" disabled={busy} onClick={() => update(path, item, item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE', 'Package')}>{item.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}</button></div>) : <p className="muted">No {title.toLowerCase()} packages yet.</p>}</section>;
}

function PaymentAdmin({ bookings, request, act, busy }) {
  const payments = useMemo(() => bookings.flatMap((booking) => (booking.payments || []).map((payment) => ({ ...payment, bookingId: booking.id, customerName: booking.customerName, bookingStatus: booking.status }))), [bookings]);
  return payments.length ? <div className="table-wrap"><table><thead><tr><th>Payment</th><th>Booking</th><th>Customer</th><th>Amount</th><th>Method</th><th>Status</th><th>Action</th></tr></thead><tbody>{payments.map((payment) => <tr key={payment.id}><td>#{payment.id}</td><td>#{payment.bookingId}</td><td>{payment.customerName}</td><td>{money(payment.amount)}</td><td>{payment.paymentMethod}</td><td><StatusPill>{payment.status}</StatusPill></td><td>{payment.status === 'PENDING' && <><button className="table-action" disabled={busy} onClick={() => { const reference = window.prompt('Enter the demo reconciliation reference:'); if (reference) act(() => request(`/admin/payments/${payment.id}`, { method: 'PATCH', body: { status: 'SUCCESS', transactionReference: reference } }), 'Demo payment marked successful.'); }}>Mark successful</button><button className="table-action danger" disabled={busy} onClick={() => act(() => request(`/admin/payments/${payment.id}`, { method: 'PATCH', body: { status: 'FAILED' } }), 'Payment marked failed.')}>Mark failed</button></>}</td></tr>)}</tbody></table><p className="table-note">Payments are demo records only. No live payment provider is connected.</p></div> : <Empty title="No payment records yet" detail="Customer demo payment records will appear here." />;
}
