import { useMemo, useState, useEffect } from 'react';

const fallbackHost = typeof window !== 'undefined'
  ? `${window.location.protocol}//${window.location.hostname}:8080`
  : 'http://localhost:8080';
const API_BASE = import.meta.env.VITE_API_BASE_URL || fallbackHost;

const initialForm = {
  username: '',
  email: '',
  password: ''
};

const modeCopy = {
  login: {
    title: 'Welcome back',
    subtitle: 'Sign in to access curated reads and your private library.',
    cta: 'Sign in'
  },
  register: {
    title: 'Create your account',
    subtitle: 'Join Bookfinder in less than a minute.',
    cta: 'Create account'
  },
  forgot: {
    title: 'Recover access',
    subtitle: 'We will send a reset link if the email exists.',
    cta: 'Send reset'
  }
};

export default function App() {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState(initialForm);
  const [status, setStatus] = useState({ message: '', error: '', loading: false });
  const [session, setSession] = useState(null);
  const [books, setBooks] = useState([]);
  const [booksError, setBooksError] = useState('');

  const copy = useMemo(() => modeCopy[mode], [mode]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const switchMode = (nextMode) => {
    setMode(nextMode);
    setForm(initialForm);
    setStatus({ message: '', error: '', loading: false });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setStatus({ message: '', error: '', loading: true });

    try {
      let endpoint = '/api/auth/login';
      let payload = {
        usernameOrEmail: form.username,
        password: form.password
      };

      if (mode === 'register') {
        endpoint = '/api/auth/register';
        payload = {
          username: form.username,
          email: form.email,
          password: form.password
        };
      }

      if (mode === 'forgot') {
        endpoint = '/api/auth/forgot';
        payload = { email: form.email };
      }

      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(data.message || 'Request failed. Please try again.');
      }

      if (mode === 'login') {
        setSession({
          username: data.username || form.username,
          role: data.role || 'USER'
        });
        setStatus({ message: data.message || 'Welcome back.', error: '', loading: false });
      } else if (mode === 'register') {
        setMode('login');
        setForm(initialForm);
        setStatus({ message: data.message || 'Registration successful. Please sign in.', error: '', loading: false });
      } else {
        setStatus({ message: data.message || 'If the email exists, reset instructions were sent.', error: '', loading: false });
      }
    } catch (err) {
      setStatus({ message: '', error: err.message || 'Something went wrong.', loading: false });
    }
  };

  const fetchBooks = async () => {
    setBooksError('');
    try {
      const response = await fetch(`${API_BASE}/api/books`);
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Unable to load books.');
      }
      setBooks(data);
    } catch (err) {
      setBooksError(err.message || 'Unable to load books.');
    }
  };

  useEffect(() => {
    if (session) {
      fetchBooks();
    }
  }, [session]);

  const handleLogout = () => {
    setSession(null);
    setBooks([]);
    setMode('login');
    setForm(initialForm);
    setStatus({ message: '', error: '', loading: false });
  };

  return (
    <div className="app">
      <div className="orb orb--one" />
      <div className="orb orb--two" />
      <div className="orb orb--three" />

      <main className="shell">
        <section className="panel panel--info">
          <div className="eyebrow">Bookfinder Studio</div>
          <h1 className="hero-title">A refined portal for readers and librarians.</h1>
          <p className="hero-subtitle">
            Everything in one place: fast login, seamless discovery, and a curated experience that feels
            like premium publishing software.
          </p>

          <div className="stats">
            <div>
              <span>120k+</span>
              <small>Monthly readers</small>
            </div>
            <div>
              <span>4.9</span>
              <small>Average rating</small>
            </div>
            <div>
              <span>36</span>
              <small>Curated lists</small>
            </div>
          </div>

          <div className="info-card">
            <div>
              <strong>Editorial picks</strong>
              <p>Weekly highlights with fresh covers and flexible metadata.</p>
            </div>
            <div className="info-line" />
            <div>
              <strong>Secure access</strong>
              <p>Admin and user roles with clean, minimal onboarding.</p>
            </div>
          </div>
        </section>

        <section className="panel panel--card">
          <div className="card">
            <div className="brand">
              <span className="brand-mark">BF</span>
              <div>
                <div className="brand-name">Bookfinder</div>
                <div className="brand-tag">Knowledge, curated</div>
              </div>
            </div>

            {!session && (
              <>
                <h2>{copy.title}</h2>
                <p className="subtitle">{copy.subtitle}</p>

                <div className="mode-toggle">
                  <button
                    type="button"
                    className={mode === 'login' ? 'active' : ''}
                    onClick={() => switchMode('login')}
                  >
                    Login
                  </button>
                  <button
                    type="button"
                    className={mode === 'register' ? 'active' : ''}
                    onClick={() => switchMode('register')}
                  >
                    Register
                  </button>
                  <button
                    type="button"
                    className={mode === 'forgot' ? 'active' : ''}
                    onClick={() => switchMode('forgot')}
                  >
                    Forgot Password
                  </button>
                </div>

                <form className="form" onSubmit={handleSubmit}>
                  {mode !== 'forgot' && (
                    <label className="field">
                      <span>Username {mode === 'login' ? 'or Email' : ''}</span>
                      <input
                        name="username"
                        value={form.username}
                        onChange={handleChange}
                        placeholder="Enter your username"
                        required
                      />
                    </label>
                  )}

                  {(mode === 'register' || mode === 'forgot') && (
                    <label className="field">
                      <span>Email</span>
                      <input
                        name="email"
                        type="email"
                        value={form.email}
                        onChange={handleChange}
                        placeholder="you@example.com"
                        required
                      />
                    </label>
                  )}

                  {mode !== 'forgot' && (
                    <label className="field">
                      <span>Password</span>
                      <input
                        name="password"
                        type="password"
                        value={form.password}
                        onChange={handleChange}
                        placeholder="Enter your password"
                        required
                      />
                    </label>
                  )}

                  <button className="primary" type="submit" disabled={status.loading}>
                    {status.loading ? 'Please wait...' : copy.cta}
                  </button>
                </form>
              </>
            )}

            {session && (
              <>
                <h2>Library access</h2>
                <p className="subtitle">
                  Welcome {session.username}. Role: <span className="badge">{session.role}</span>
                </p>
                {status.message && <div className="message success">{status.message}</div>}
                {booksError && <div className="message error">{booksError}</div>}

                <div className="book-grid">
                  {books.map((book) => (
                    <article className="book-card" key={book.id}>
                      <div
                        className="book-cover"
                        style={{
                          backgroundImage: book.coverUrl
                            ? `url(${book.coverUrl})`
                            : 'linear-gradient(140deg, #ffd6c0, #f6b098)'
                        }}
                      />
                      <div className="book-body">
                        <h3>{book.title}</h3>
                        <p className="book-meta">
                          {book.author} · {book.publishedYear}
                        </p>
                        <p className="book-desc">{book.description}</p>
                        <span className="book-tag">{book.genre}</span>
                      </div>
                    </article>
                  ))}
                </div>

                <button className="ghost" type="button" onClick={handleLogout}>
                  Log out
                </button>
              </>
            )}

            {status.error && <div className="message error">{status.error}</div>}
            {!session && status.message && <div className="message success">{status.message}</div>}

            <p className="footnote">Secure sign-in for an editorial-grade library experience.</p>
          </div>
        </section>
      </main>
    </div>
  );
}
